package it.unical.unijira.utils;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration;
import org.modelmapper.convention.MatchingStrategies;
import org.modelmapper.convention.NameTokenizers;
import org.modelmapper.module.jdk8.Jdk8Module;
import org.modelmapper.module.jsr310.Jsr310Module;
import org.modelmapper.module.jsr310.Jsr310ModuleConfig;

import javax.persistence.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
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
        this.getConfiguration().setMethodAccessLevel(Configuration.AccessLevel.PUBLIC);
        this.getConfiguration().setSourceNameTokenizer(NameTokenizers.CAMEL_CASE);
        this.getConfiguration().setDestinationNameTokenizer(NameTokenizers.CAMEL_CASE);
        this.getConfiguration().setUseOSGiClassLoaderBridging(true);

        this.registerModule(new Jsr310Module(new Jsr310ModuleConfig()));
        this.registerModule(new Jdk8Module());

    }


    private <T> Object resolveId(T entity) throws IllegalAccessException, IllegalArgumentException, NullPointerException {

        Objects.requireNonNull(entity);

        for(var field : entity.getClass().getDeclaredFields()) {

            if(!field.isAnnotationPresent(Id.class) && !field.isAnnotationPresent(EmbeddedId.class))
                continue;

            field.setAccessible(true);

            if(field.isAnnotationPresent(EmbeddedId.class))
                return Objects.requireNonNull(resolveEntity(field.get(entity)));

            return Objects.requireNonNull(field.get(entity));

        }

        throw new IllegalArgumentException("Entity " + entity.getClass().getName() + " has no id field");

    }



    @SuppressWarnings("unchecked")
    private <T> T resolveEntity(T entity) throws NullPointerException {

        Objects.requireNonNull(entity);

        if(!entity.getClass().isAnnotationPresent(Entity.class) && !entity.getClass().isAnnotationPresent(Embeddable.class))
            return entity;


            for (var field : entity.getClass().getDeclaredFields()) {

                if (Collection.class.isAssignableFrom(field.getType())) {


                    if(field.getType().isAnnotationPresent(Embeddable.class)) {

                        field.setAccessible(true);

                        try {

                            resolveEntity(field.get(entity));

                        } catch (IllegalAccessException ignored) { }

                    } else if(Collection.class.isAssignableFrom(field.getType())) {

                        try {

                            Collection<Object> items = (Collection<Object>) Objects.requireNonNull(field.get(entity));
                            List<Object> newElements = new ArrayList<>();

                            for(var item : items) {

                                if(item.getClass().isAnnotationPresent(Entity.class))
                                    newElements.add(Objects.requireNonNull(entityManager.find(item.getClass(), resolveId(item))));

                                else if(item.getClass().isAnnotationPresent(Embeddable.class))
                                    newElements.add(Objects.requireNonNull(resolveEntity(item)));

                                else
                                    newElements.add(item);
                            }

                            items.clear();
                            items.addAll(newElements);

                    } catch (IllegalAccessException | IllegalArgumentException | NullPointerException ignored) { }

                } else if(field.getType().isAnnotationPresent(Entity.class)) {

                    try {

                        field.set(entity, Objects.requireNonNull(entityManager.find(field.getType(), resolveId(field.get(entity)))));

                    } catch (IllegalAccessException | IllegalArgumentException | NullPointerException ignored) {}

                }

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
