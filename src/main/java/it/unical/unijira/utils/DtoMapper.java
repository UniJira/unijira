package it.unical.unijira.utils;

import it.unical.unijira.data.dto.AbstractBaseDTO;
import it.unical.unijira.data.models.AbstractBaseEntity;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration;
import org.modelmapper.convention.MatchingStrategies;
import org.modelmapper.convention.NameTokenizers;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.Id;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
        this.getConfiguration().setMethodAccessLevel(Configuration.AccessLevel.PUBLIC);
        this.getConfiguration().setSourceNameTokenizer(NameTokenizers.CAMEL_CASE);
        this.getConfiguration().setDestinationNameTokenizer(NameTokenizers.CAMEL_CASE);
        this.getConfiguration().setUseOSGiClassLoaderBridging(true);

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
    private <T> T resolveEntity(Object source, T entity) throws NullPointerException {

        Objects.requireNonNull(entity);

        if(entity.getClass().isAnnotationPresent(Entity.class)) {

            for (var field : entity.getClass().getDeclaredFields()) {

                if (Collection.class.isAssignableFrom(field.getType())) {

                    field.setAccessible(true);

                    try {

                        Collection<Object> items = (Collection<Object>) Objects.requireNonNull(field.get(entity));

                        for (var item : items) {

                            if (!item.getClass().isAnnotationPresent(Entity.class))
                                continue;

                            items.add(Objects.requireNonNull(entityManager.find(item.getClass(), resolveId(item))));
                            items.remove(item);

                        }

                    } catch (IllegalAccessException | IllegalArgumentException | NullPointerException ignored) {
                    }


                } else if (field.getType().isAnnotationPresent(Entity.class)) {

                    field.setAccessible(true);

                    try {

                        field.set(entity, Objects.requireNonNull(entityManager.find(field.getType(), resolveId(field.get(entity)))));

                    } catch (IllegalAccessException | IllegalArgumentException | NullPointerException ignored) {
                    }

                }

            }

        }


        if(entity instanceof AbstractBaseEntity e && source instanceof AbstractBaseDTO dto) {

            try {

                e.setCreatedAt(LocalDateTime.parse(dto.getCreatedAt()));
                e.setUpdatedAt(LocalDateTime.parse(dto.getUpdatedAt()));

            } catch (Exception ignored) { }

        }

        if(source instanceof AbstractBaseEntity e && entity instanceof AbstractBaseDTO dto) {

            try {

                dto.setCreatedAt(e.getCreatedAt().format(DateTimeFormatter.ISO_DATE_TIME));
                dto.setUpdatedAt(e.getUpdatedAt().format(DateTimeFormatter.ISO_DATE_TIME));

            } catch (Exception ignored) { }

        }


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
