import java.util.ArrayList;
import java.util.List;

public class CleanCode {

    void g() {
        List<Circle> circles = new ArrayList();
//        f(circles);
    }

    private void f(List<Shape> circle) {
        circle.add(new Square());
    }

    private void aaa() {
        bbb(new Circle());
    }

    private void bbb(Shape shape) {

    }

}
