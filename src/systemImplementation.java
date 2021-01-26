public class systemImplementation extends java.rmi.server.UnicastRemoteObject implements systemInterface
{

        // Implementations must have an explicit constructor
        // in order to declare the RemoteException exception

    public systemImplementation()
        throws java.rmi.RemoteException {
            super();
        }

        // Implementation of the add method
        // The two long parameters are added added and the result is retured
        public String testMessage()
        throws java.rmi.RemoteException {
            return "Working fine";
        }
}
