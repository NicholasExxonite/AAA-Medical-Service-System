import java.rmi.Naming;

public class systemServer {
    public systemServer() {

        //Construct a new CalculatorImpl object and bind it to the local rmiregistry
        //N.b. it is possible to host multiple objects on a server by repeating the
        //following method.

        try {
            systemImplementation obj  = new systemImplementation();
            Naming.rebind("systemInterface", obj);
        }
        catch (Exception e) {
            System.out.println("Server Error: " + e);
        }
    }

    public static void main(String args[]) {
        //Create the new Calculator server
        new systemServer();
    }
}
