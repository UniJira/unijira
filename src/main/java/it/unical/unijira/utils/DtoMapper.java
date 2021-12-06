package it.unical.unijira.utils;

import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration;
import org.modelmapper.convention.MatchingStrategies;
import org.modelmapper.convention.NameTokenizers;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.Id;
import java.util.Objects;

public class DtoMapper extends ModelMapper {

    private final EntityManager entityManager;

    public DtoMapper(EntityManager entityManager) {

        this.entityManager = entityManager;

        this.getConfiguration().setMatchingStrategy(MatchingStrategies.STANDARD);
        this.getConfiguration().setFieldMatchingEnabled(true);
        this.getConfiguration().setFieldAccessLevel(Configuration.AccessLevel.PRIVATE);
        this.getConfiguration().setSourceNameTokenizer(NameTokenizers.CAMEL_CASE);
        this.getConfiguration().setDestinationNameTokenizer(NameTokenizers.CAMEL_CASE);

    }


    private <T> Object resolveId(T entity) throws IllegalAccessException {

        if(entity == null)
            throw new NullPointerException();

        for(var field : entity.getClass().getDeclaredFields()) {

            if(field.getAnnotation(Id.class) == null)
                continue;

            field.setAccessible(true);

            return Objects.requireNonNull(field.get(entity));

        }

        throw new IllegalArgumentException("Entity " + entity.getClass().getName() + " has no id field");

    }


    private <T> T resolveEntity(T entity) {

        for(var field : entity.getClass().getDeclaredFields()) {

            if(field.getType().getAnnotation(Entity.class) == null)
                continue;

            try {

                field.setAccessible(true);
                field.set(entity, Objects.requireNonNull(entityManager.find(field.getType(), resolveId(entity))));

            } catch (IllegalAccessException | IllegalArgumentException | NullPointerException ignored) {}

        }

        return entity;

    }


    public <T> T map(Object source, Class<T> destinationClass) {
        return resolveEntity(super.map(source, destinationClass));
    }

}
