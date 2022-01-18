package it.unical.unijira.utils;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration;
import org.modelmapper.convention.MatchingStrategies;
import org.modelmapper.convention.NameTokenizers;
import org.modelmapper.module.jdk8.Jdk8Module;
import org.modelmapper.module.jsr310.Jsr310Module;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.Id;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Objects;

@Slf4j
public class DtoMapper extends ModelMapper {

    private final EntityManager entityManager;

    public DtoMapper(EntityManager entityManager) {

        this.entityManager = entityManager;

        this.getConfiguration().setMatchingStrategy(MatchingStrategies.STANDARD);
        this.getConfiguration().setFieldMatchingEnabled(true);
        this.getConfiguration().setCollectionsMergeEnabled(true);
        this.getConfiguration().setDeepCopyEnabled(true);
        this.getConfiguration().setSkipNullEnabled(true);
        this.getConfiguration().setImplicitMappingEnabled(true);
        this.getConfiguration().setFieldAccessLevel(Configuration.AccessLevel.PRIVATE);
        this.getConfiguration().setSourceNameTokenizer(NameTokenizers.CAMEL_CASE);
        this.getConfiguration().setDestinationNameTokenizer(NameTokenizers.CAMEL_CASE);

        this.registerModule(new Jsr310Module());
        this.registerModule(new Jdk8Module());
    }


    private <T> Object resolveId(T entity) throws IllegalAccessException, IllegalArgumentException, NullPointerException {

        Objects.requireNonNull(entity);

        for(var field : entity.getClass().getDeclaredFields()) {

            if(!field.isAnnotationPresent(Id.class))
                continue;

            field.setAccessible(true);

            return Objects.requireNonNull(field.get(entity));

        }

        throw new IllegalArgumentException("Entity " + entity.getClass().getName() + " has no id field");

    }



    @SuppressWarnings("unchecked")
    private <T> T resolveEntity(T entity) throws NullPointerException {

        Objects.requireNonNull(entity);

        if(!entity.getClass().isAnnotationPresent(Entity.class))
            return entity;


        for(var field : entity.getClass().getDeclaredFields()) {

            if(Collection.class.isAssignableFrom(field.getType())) {

                field.setAccessible(true);

                try {

                    Collection<Object> items = (Collection<Object>) Objects.requireNonNull(field.get(entity));

                    for(var item : items) {

                        if(!item.getClass().isAnnotationPresent(Entity.class))
                            continue;

                        items.add(Objects.requireNonNull(entityManager.find(item.getClass(), resolveId(item))));
                        items.remove(item);

                    }

                } catch (IllegalAccessException | IllegalArgumentException | NullPointerException ignored) { }


            } else if(field.getType().isAnnotationPresent(Entity.class)) {

                field.setAccessible(true);

                try {

                    field.set(entity, Objects.requireNonNull(entityManager.find(field.getType(), resolveId(field.get(entity)))));

                } catch (IllegalAccessException | IllegalArgumentException | NullPointerException ignored) { }

            }

        }

        return entity;

    }

    @Override
    public <T> T map(Object source, Class<T> destinationClass) {
        return resolveEntity(super.map(source, destinationClass));
    }

    @Override
    public <D> D map(Object source, Class<D> destinationType, String typeMapName) {
        return resolveEntity(super.map(source, destinationType, typeMapName));
    }

    @Override
    public <D> D map(Object source, Type destinationType) {
        return resolveEntity(super.map(source, destinationType));
    }

    @Override
    public <D> D map(Object source, Type destinationType, String typeMapName) {
        return resolveEntity(super.map(source, destinationType, typeMapName));
    }

}
