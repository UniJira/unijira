package it.unical.unijira.controllers;

import it.unical.unijira.UniJiraTest;
import it.unical.unijira.data.dao.UserRepository;
import it.unical.unijira.data.dao.projects.MembershipRepository;
import it.unical.unijira.data.dao.projects.ProjectRepository;
import it.unical.unijira.data.models.User;
import it.unical.unijira.data.models.projects.Membership;
import it.unical.unijira.data.models.projects.MembershipKey;
import it.unical.unijira.data.models.projects.Project;
import it.unical.unijira.services.common.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserControllerTest extends UniJiraTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private MembershipRepository membershipRepository;

    @Autowired
    private UserService userService;

    private Long userId;


    @BeforeEach
    public void initDataForTest() {

        User user = userRepository.findByUsername(UniJiraTest.USERNAME).orElse(null);
        this.userId = user.getId();
        Project p = new Project();
        p.setName("PROGETTONE PARI");
        p.setKey("zzP");
        p =projectRepository.save(p);

        Project pDispari = new Project();
        pDispari.setName("PROGETTONE DISPARI");
        pDispari.setKey("zzD");
        pDispari=projectRepository.save(pDispari);

        MembershipKey mkey = new MembershipKey();
        mkey.setUser(user);
        mkey.setProject(p);
        Membership m = new Membership();
        m.setKey(mkey);
        m.setRole(Membership.Role.MEMBER);
        m.setStatus(Membership.Status.ENABLED);
        membershipRepository.save(m);


        mkey = new MembershipKey();
        mkey.setUser(user);
        mkey.setProject(pDispari);
        m = new Membership();
        m.setKey(mkey);
        m.setRole(Membership.Role.MEMBER);
        m.setStatus(Membership.Status.ENABLED);
        membershipRepository.save(m);

        for (int i=0; i < 7; i++) {
            String username= "Utente"+i+"@gmail.com";
            User userTmp = userService.findByUsername(username).orElse(null);
            if(userTmp==null) {
                userTmp = new User();
                userTmp.setUsername(username);
                userTmp.setPassword("123456");
                userTmp.setStatus(User.Status.ACTIVE);
                userTmp = userRepository.saveAndFlush(userTmp);
            }
            mkey = new MembershipKey();
            mkey.setUser(userTmp);
            if (i%2 == 0) {
                mkey.setProject(p);
            }
            else {
                mkey.setProject(pDispari);
            }
            m = new Membership();
            m.setKey(mkey);
            m.setRole(Membership.Role.MEMBER);
            m.setStatus(Membership.Status.ENABLED);
            membershipRepository.save(m);
        }
    }
    @Test
    public void testCollaborators() throws Exception {

        String username = "Utente99@gmail.com";
        User userTmp = new User();
        userTmp.setUsername(username);
        userTmp.setPassword("123456");
        userTmp.setStatus(User.Status.ACTIVE);
        userTmp = userRepository.save(userTmp);

        ResultActions call = mockMvc.perform(get("/users/"+userId+"/collaborators")
                        .header("Authorization", "Bearer "
                                + this.performLogin(UniJiraTest.USERNAME, UniJiraTest.PASSWORD)));
        MvcResult returnValue = call.andReturn();
        String result = returnValue.getResponse().getContentAsString();
        System.out.println(result);
        call.andExpect(status().isOk());

        Assertions.assertTrue(result.contains("\"username\":\"Utente0@gmail.com\"") &&
                            result.contains("\"username\":\"Utente1@gmail.com\"") &&
                            result.contains(("\"username\":\"Utente2@gmail.com\"")) &&
                            result.contains("\"username\":\"Utente3@gmail.com\"")&&
                            result.contains("\"username\":\"Utente4@gmail.com\"") &&
                            result.contains("\"username\":\"Utente5@gmail.com\"") &&
                            result.contains("\"username\":\"Utente6@gmail.com\""));

    }

    @Test
    public void testProjects() throws Exception {
        ResultActions call = mockMvc.perform(get("/users/"+userId+"/projects")
                .header("Authorization", "Bearer "
                        + this.performLogin(UniJiraTest.USERNAME, UniJiraTest.PASSWORD)));
        MvcResult returnValue = call.andReturn();
        String result = returnValue.getResponse().getContentAsString();
        System.out.println(result);
        call.andExpect(status().isOk());

        Assertions.assertTrue(result.contains("\"key\":\"zzP\"") && result.contains("\"key\":\"zzD\","));


    }



}
