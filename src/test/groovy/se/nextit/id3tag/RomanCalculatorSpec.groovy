import se.nextit.id3tag.RomanCalculator
import spock.lang.Specification;

class RomanCalculatorSpec extends Specification {
    def "I plus I should equal II"() {
        given:
        def calculator = new RomanCalculator()
        when:
        def result = calculator.add("I", "I")
        then:
        result == "II"
    }
}
 