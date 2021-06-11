

package thunder.mockclass;




class MockingClass {

}


public class MockClassAsParameterCompleterTest {


    private void hello(MockingClass mockclass) {

    }

    public void test() {
        hello(MockingClass.m<caret>);
    }


}