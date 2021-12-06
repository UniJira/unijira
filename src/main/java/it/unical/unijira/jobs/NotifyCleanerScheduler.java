package it.unical.unijira.jobs;

import it.unical.unijira.data.dao.NotifyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.ApplicationScope;

@Component
@ApplicationScope
public class NotifyCleanerScheduler {

    private final NotifyRepository notifyRepository;

    @Autowired
    public NotifyCleanerScheduler(NotifyRepository notifyRepository) {
        this.notifyRepository = notifyRepository;
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void execute() {
        notifyRepository.deleteAll(notifyRepository.findAll((root, query, cb) -> cb.equal(root.get("read"), false)));
    }

}
