package it.unical.unijira.services.common;

import it.unical.unijira.data.models.Sprint;
import it.unical.unijira.data.models.User;
import it.unical.unijira.data.models.items.Item;

import java.util.List;

public interface HintService {


    List<Item> sendHint(Sprint sprintObj, User userObj, String type);
}
