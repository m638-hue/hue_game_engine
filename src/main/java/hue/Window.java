package hue;

import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import util.Time;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {
    private int width, height;
    private String title;
    private long glfwWindow; //Memory address for window

    private static Window window = null;
    private static Scene currentScene = null;

    private Window(){
        this.width = 500;
        this.height = 500;
        this.title = "Rogue";
    }

    public static void changeScene (int newScene) {
        switch (newScene) {
            case 0 :
                currentScene = new LevelEditorScene();
                currentScene.init();
                break;

            case 1 :
                currentScene = new LevelScene();
                currentScene.init();
                break;

            default :
                assert false : "Unknown scene " + newScene;
                break;
        }
    }

    public static Window get() {
        if (Window.window == null) Window.window = new Window();

        return Window.window;
    }

    public void run() {
        System.out.println("Hello LWJGL " + Version.getVersion() + "!!");

        init();
        loop();

        //Free memory
        glfwFreeCallbacks(glfwWindow);
        glfwDestroyWindow(glfwWindow);

        //Terminate glfw and free error callback
        //Not necessary but proper
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    public void init() {
        //Error callback
        GLFWErrorCallback.createPrint(System.err).set();

        //Initialize GLFW
        if(!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW.");
        }

        //Configure GLFW
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);
        glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE);

        //Create window
        glfwWindow = glfwCreateWindow(this.width, this.height, this.title, NULL, NULL);
        if (glfwWindow == NULL) throw new IllegalStateException("Failed to create GLFW window");

        //Set callbacks
        glfwSetCursorPosCallback(glfwWindow, MouseListener :: mousePosCallBack);
        glfwSetMouseButtonCallback(glfwWindow, MouseListener :: mouseButtonCalBack);
        glfwSetScrollCallback(glfwWindow, MouseListener :: mouseScrollCallBack);
        glfwSetKeyCallback(glfwWindow, KeyListener :: keyCallback);

        //Make OpenGL context current
        glfwMakeContextCurrent(glfwWindow);

        //Enable v-sync
        glfwSwapInterval(1);

        //Make the window visible
        glfwShowWindow(glfwWindow);

        //Set bindings with C
        GL.createCapabilities();
    }

    public void loop() {
        float beginTime = Time.getTime();
        float endTime = Time.getTime();
        float dt = -1.0f;
        while (!glfwWindowShouldClose(glfwWindow)) {

            glfwPollEvents();
            glClearColor(1.0f, 1.0f,1.0f, 1.0f);
            glClear(GL_COLOR_BUFFER_BIT);

            glfwSwapBuffers(glfwWindow);
            endTime = Time.getTime();
            dt = endTime - beginTime;
            beginTime = endTime;
        }
    }
}
