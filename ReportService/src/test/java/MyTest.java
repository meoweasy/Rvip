import com.report.reportService.Service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;

import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest(classes = ReportService.class)
@ComponentScan(basePackages = "com.report.reportService")
public class MyTest {

//    @Autowired
//    private ReportService reportService;

    @Test
    public String testSomething() {
//        String result = reportService.check();
//
//        assertEquals("TestOk", result);
        return "Ok";
    }
}
