import glCore.core.Application;

public class Main extends Application {

    public Main(){
        pushLayer(new SandboxLayer("SandboxLayer"));
    }

    public static void main(String[] args) {
        try {
            launch(new Main());
        } catch (Exception e){
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }
}
