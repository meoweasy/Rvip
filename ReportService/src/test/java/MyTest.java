import com.report.reportService.Service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest
public class MyTest {

    @Autowired
    private ReportService reportService;

    @Test
    public void testSomething() {
        String result = reportService.check();

        assertEquals("TestOk", result);
    }
}
