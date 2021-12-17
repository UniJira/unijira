package it.unical.unijira.services.common;

import it.unical.unijira.data.models.ProductBacklogItem;
import it.unical.unijira.data.models.Sprint;

import java.util.List;

public interface SprintInsertionService {

    List<ProductBacklogItem> findItemsBySprint(Sprint s);
}
