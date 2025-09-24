package springTesting.SpringTestingPractice.services.impl;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import springTesting.SpringTestingPractice.services.DataService;

@Service
@Profile("dev")
public class DataServiceImplDev implements DataService {


    @Override
    public String getData() {
        return "Dev Data";
    }
}
