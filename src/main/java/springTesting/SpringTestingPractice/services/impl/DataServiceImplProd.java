package springTesting.SpringTestingPractice.services.impl;


import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import springTesting.SpringTestingPractice.services.DataService;

@Service
@Profile("prod")
public class DataServiceImplProd implements DataService {
    @Override
    public String getData() {
        return "Prod Data";
    }
}
