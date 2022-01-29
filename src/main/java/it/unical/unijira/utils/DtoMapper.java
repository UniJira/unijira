package it.unical.unijira.utils;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration;
import org.modelmapper.convention.MatchingStrategies;
import org.modelmapper.convention.NameTokenizers;
import org.modelmapper.module.jdk8.Jdk8Module;
import org.modelmapper.module.jsr310.Jsr310Module;

import javax.persistence.*;
import java.lang.reflect.Type;
import java.net.URL;
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
        this.getConfiguration().setSourceNameTokenizer(NameTokenizers.CAMEL_CASE);
        this.getConfiguration().setDestinationNameTokenizer(NameTokenizers.CAMEL_CASE);

        this.registerModule(new Jsr310Module());
        this.registerModule(new Jdk8Module());
    }


    private <T, S> void forceMapping(Object source, T entity, Class<S> type) {

        Objects.requireNonNull(type);

        if(Objects.isNull(source) || Objects.isNull(entity))
            return;


        for(var field : entity.getClass().getDeclaredFields()) {

            if(type.isAssignableFrom(field.getType())) {

                field.setAccessible(true);

                try {

                    var unmapped = source.getClass().getDeclaredField(field.getName());

                    if(type.isAssignableFrom(unmapped.getType())) {

                        unmapped.setAccessible(true);

                        if (unmapped.get(source) != null) {

                            field.set(entity, unmapped.get(source));

                        }

                    }

                } catch (NoSuchFieldException | IllegalAccessException ignored) { }

            }

        }
    }


    private <T> Object resolveId(Object source, T entity) throws IllegalAccessException, IllegalArgumentException, NullPointerException {

        Objects.requireNonNull(entity);

        for(var field : entity.getClass().getDeclaredFields()) {

            if(!field.isAnnotationPresent(Id.class) && !field.isAnnotationPresent(EmbeddedId.class))
                continue;

            field.setAccessible(true);

            if(field.isAnnotationPresent(EmbeddedId.class))
                return Objects.requireNonNull(resolveEntity(source, field.get(entity)));

            return Objects.requireNonNull(field.get(entity));

        }

        throw new IllegalArgumentException("Entity " + entity.getClass().getName() + " has no id field");

    }



    @SuppressWarnings("unchecked")
    private <T> T resolveEntity(Object source, T entity) throws NullPointerException {

        Objects.requireNonNull(entity);

        if(entity.getClass().isAnnotationPresent(Entity.class) || entity.getClass().isAnnotationPresent(Embeddable.class)) {


            for (var field : entity.getClass().getDeclaredFields()) {

                if (field.getType().isAnnotationPresent(Embeddable.class)) {

                    field.setAccessible(true);

                    try {

                        resolveEntity(source, field.get(entity));

                    } catch (IllegalAccessException ignored) { }

                } else if (Collection.class.isAssignableFrom(field.getType())) {

                    field.setAccessible(true);

                    try {

                        Collection<Object> items = (Collection<Object>) Objects.requireNonNull(field.get(entity));
                        List<Object> newElements = new ArrayList<>();

                        for (var item : items) {

                            if (item.getClass().isAnnotationPresent(Entity.class))
                                newElements.add(Objects.requireNonNull(entityManager.find(item.getClass(), resolveId(source, item))));

                            else if (item.getClass().isAnnotationPresent(Embeddable.class))
                                newElements.add(Objects.requireNonNull(resolveEntity(source, item)));

                            else
                                newElements.add(item);
                        }

                        items.clear();
                        items.addAll(newElements);

                    } catch (IllegalAccessException | IllegalArgumentException | NullPointerException ignored) { }

                } else if (field.getType().isAnnotationPresent(Entity.class)) {

                    field.setAccessible(true);

                    try {

                        field.set(entity, Objects.requireNonNull(entityManager.find(field.getType(), resolveId(source, field.get(entity)))));

                    } catch (IllegalAccessException | IllegalArgumentException | NullPointerException ignored) { }

                }

            }

        }


        this.forceMapping(source, entity, URL.class);

        return entity;

    }

    @Override
    public <T> T map(Object source, Class<T> destinationClass) {
        return resolveEntity(source, super.map(source, destinationClass));
    }

    @Override
    public <D> D map(Object source, Class<D> destinationType, String typeMapName) {
        return resolveEntity(source, super.map(source, destinationType, typeMapName));
    }

    @Override
    public <D> D map(Object source, Type destinationType) {
        return resolveEntity(source, super.map(source, destinationType));
    }

    @Override
    public <D> D map(Object source, Type destinationType, String typeMapName) {
        return resolveEntity(source, super.map(source, destinationType, typeMapName));
    }

}