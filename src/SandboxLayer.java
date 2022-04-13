import com.badlogic.ashley.core.Entity;
import editor.SceneHierarchyPanel;
import glCore.core.Application;
import glCore.core.Layer;
import glCore.core.Time;
import glCore.events.Event;
import glCore.events.EventType;
import glCore.events.applicationEvents.WindowResizeEvent;
import glCore.renderer.*;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.ImGuiViewport;
import imgui.ImVec2;
import imgui.flag.ImGuiConfigFlags;
import imgui.flag.ImGuiDockNodeFlags;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL33;
import editor.EditorCamera;
import scene.Scene;
import scene.components.TransformComponent;
import scene.components.renderering.*;
import scene.sytems.RenderingSystem;
import utils.AssetManager;

public class SandboxLayer extends Layer {

    private Scene _scene;

    private EditorCamera _camera;
    private SceneHierarchyPanel _sceneHierarchyPanel;

    private Framebuffer _msFramebuffer;
    private Framebuffer _framebuffer;

    private boolean _viewportFocus = true;
    private float _sceneViewportWidth = 1280.0f;
    private float _sceneViewportHeight = 720.0f;

    public SandboxLayer(String name) {
        super(name);
    }

    @Override
    public void onAttach() {

        GL33.glEnable(GL33.GL_DEPTH_TEST);
        GL33.glEnable(GL33.GL_CULL_FACE);

        _camera = new EditorCamera();;
        _msFramebuffer = new Framebuffer(
                Application.get().getWindow().getWidth(),
                Application.get().getWindow().getHeight(),
                4, true);

        _framebuffer = new Framebuffer(
                Application.get().getWindow().getWidth(),
                Application.get().getWindow().getHeight(),
                1, false);

        _scene = new Scene();
        _scene.addSystem(new RenderingSystem(_camera, _msFramebuffer));
        _sceneHierarchyPanel = new SceneHierarchyPanel(_scene);

        float[] cubeVert = new float[]{
                // pos                // texCoord

                // back
                0.5f, -0.5f, -0.5f,  0.0f, 0.0f,  0.0f, 0.0f, -1.0f,
                -0.5f, -0.5f, -0.5f,  1.0f, 0.0f, 0.0f, 0.0f, -1.0f,
                -0.5f,  0.5f, -0.5f,  1.0f, 1.0f, 0.0f, 0.0f, -1.0f,
                0.5f,  0.5f, -0.5f,  0.0f, 1.0f,  0.0f, 0.0f, -1.0f,

                // front
                -0.5f, -0.5f,  0.5f,  0.0f, 0.0f,   0.0f, 0.0f, 1.0f,
                0.5f, -0.5f,  0.5f,  1.0f, 0.0f,    0.0f, 0.0f, 1.0f,
                0.5f,  0.5f,  0.5f,  1.0f, 1.0f,    0.0f, 0.0f, 1.0f,
                -0.5f,  0.5f,  0.5f,  0.0f, 1.0f,   0.0f, 0.0f, 1.0f,

                // bottom
                -0.5f, -0.5f, -0.5f,  0.0f, 0.0f,   0.0f, -1.0f, 0.0f,
                0.5f, -0.5f, -0.5f,  1.0f, 0.0f,    0.0f, -1.0f, 0.0f,
                0.5f, -0.5f,  0.5f,  1.0f, 1.0f,    0.0f, -1.0f, 0.0f,
                -0.5f, -0.5f,  0.5f,  0.0f, 1.0f,   0.0f, -1.0f, 0.0f,

                // top
                -0.5f,  0.5f,  0.5f,  0.0f, 0.0f,   0.0f, 1.0f, 0.0f,
                0.5f,  0.5f,  0.5f,  1.0f, 0.0f,    0.0f, 1.0f, 0.0f,
                0.5f,  0.5f, -0.5f,  1.0f, 1.0f,    0.0f, 1.0f, 0.0f,
                -0.5f,  0.5f, -0.5f,  0.0f, 1.0f,   0.0f, 1.0f, 0.0f,

                // left
                -0.5f, -0.5f, -0.5f,  0.0f, 0.0f,   -1.0f, 0.0f, 0.0f,
                -0.5f, -0.5f,  0.5f,  1.0f, 0.0f,   -1.0f, 0.0f, 0.0f,
                -0.5f,  0.5f,  0.5f,  1.0f, 1.0f,   -1.0f, 0.0f, 0.0f,
                -0.5f,  0.5f, -0.5f,  0.0f, 1.0f,   -1.0f, 0.0f, 0.0f,

                // right
                0.5f, -0.5f,  0.5f,  0.0f, 0.0f,    1.0f, 0.0f, 0.0f,
                0.5f, -0.5f, -0.5f,  1.0f, 0.0f,    1.0f, 0.0f, 0.0f,
                0.5f,  0.5f, -0.5f,  1.0f, 1.0f,    1.0f, 0.0f, 0.0f,
                0.5f,  0.5f,  0.5f,  0.0f, 1.0f,    1.0f, 0.0f, 0.0f
        };

        int[] cubeIndices = new int[]{
                // back
                0, 1, 2,
                2, 3, 0,

                // front
                4, 5, 6,
                6, 7, 4,

                // bottom
                16, 17, 18,
                18, 19, 16,

                // top
                20, 21, 22,
                22, 23, 20,

                // left
                8, 9, 10,
                10, 11, 8,

                // right
                12, 13, 14,
                14, 15, 12,
        };

        var vao = new VertexArray();
        VertexBufferLayout vBufLayout = new VertexBufferLayout();
        vBufLayout.pushElements(0, 3, false);
        vBufLayout.pushElements(1, 2, false);
        vBufLayout.pushElements(2, 3, false);
        vao.addVertexBuffer(new VertexBuffer(cubeVert, vBufLayout));
        vao.addIndexBuffer(new IndexBuffer(cubeIndices));

        Entity cubeA = _scene.createEntity("cubeA");
        cubeA.getComponent(TransformComponent.class).scale = new Vector3f(30.0f, 0.5f, 30.0f);
        cubeA.getComponent(TransformComponent.class).position.y = -1.0f;
        cubeA.addAndReturn(new MeshComponent()).vao = vao;
        cubeA.addAndReturn(new MeshRendererComponent());
        var cubeAMat = cubeA.addAndReturn(new MaterialComponent());
        cubeAMat.color = new Vector4f(1.0f);
        cubeAMat.diffuseMap = AssetManager.loadAndAddTexture2D("court", "D:/Textures/basketball_court_floor.jpg", true,
                    GL33.GL_LINEAR_MIPMAP_LINEAR, GL33.GL_LINEAR, GL33.GL_REPEAT, GL33.GL_REPEAT);
        cubeAMat.specularMap = AssetManager.getTexture2D("court");
        cubeAMat.normalMap = null;
        cubeAMat.shininess = 128.0f;
        cubeAMat.tiling = new Vector2f(10.0f, 10.0f);

/*        var dirLightEntity = _scene.createEntity("dirLight");
        dirLightEntity.getComponent(TransformComponent.class).rotation = new Vector3f(-50.0f, -30.0f, 0.0f);
        var dirLight = dirLightEntity.addAndReturn(new DirectionalLightComponent());
        dirLight.color = new Vector3f(1.0f, 1.0f, 1.0f);*/

        var pointLightEntity = _scene.createEntity("pointLight");
        pointLightEntity.getComponent(TransformComponent.class).position = new Vector3f(0.0f, 8.0f, -4.0f);
        var pointLight = pointLightEntity.addAndReturn(new PointLightComponent());
        pointLight.color = new Vector3f(1.0f, 0.0f, 1.0f);

        var spotLightEntity = _scene.createEntity("spotLight");
        var plTrans = spotLightEntity.getComponent(TransformComponent.class);
        plTrans.position = new Vector3f(0.0f, 8.0f, 0.0f);
        plTrans.rotation = new Vector3f(-90.0f, 0.0f, 0.0f);
        spotLightEntity.addAndReturn(new SpotLightComponent()).color = new Vector3f(0.0f, 1.0f, 1.0f);
    }

    @Override
    public void onDetach() {
        _msFramebuffer.destroy();
        _framebuffer.destroy();
        _scene.destroyEntities();
        _scene.removeSystems();
        AssetManager.destroyAssets();
    }

    @Override
    public void onEvent(Event event) {
        if(event.getEventType() == EventType.WindowResize){
            var wr = (WindowResizeEvent)event;
            if(wr.getWidth() > 0 && wr.getHeight() > 0){
                Application.get().getWindow().setViewport(0, 0, wr.getWidth(), wr.getHeight());
            }
        }

        _camera.onEvent(event);
    }

    @Override
    public void onUpdate() {

        if(_viewportFocus)
            _camera.onUpdate();
        else
            _camera.reset();

        if(_sceneViewportWidth > 0.0f && _sceneViewportHeight > 0.0f &&
                (_msFramebuffer.getWidth() != _sceneViewportWidth ||
                 _msFramebuffer.getHeight() != _sceneViewportHeight)){

            _msFramebuffer.resize((int)_sceneViewportWidth, (int)_sceneViewportHeight);
            _framebuffer.resize((int)_sceneViewportWidth, (int)_sceneViewportHeight);
            _camera.setViewportSize(_sceneViewportWidth, _sceneViewportHeight);
        }

        _scene.update(Time.delta());
        _framebuffer.blit(_msFramebuffer, GL33.GL_COLOR_BUFFER_BIT, GL33.GL_NEAREST);
    }

    @Override
    public void onImGuiRender() {

        dockSpaceBegin();

        // Viewport
        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 0.0f, 0.0f);
        ImGui.begin("Viewport");
        _viewportFocus = ImGui.isWindowFocused() && ImGui.isWindowHovered();
        Application.get().getImGuiLayer().blockEvents(!_viewportFocus);
        ImVec2 viewportPanelSize = ImGui.getContentRegionAvail();
        _sceneViewportWidth = viewportPanelSize.x;
        _sceneViewportHeight = viewportPanelSize.y;
        ImGui.image(_framebuffer.getColorAttachmentRendererID(),
                _sceneViewportWidth, _sceneViewportHeight, 0, 1, 1, 0);
        ImGui.end();
        ImGui.popStyleVar();

        _sceneHierarchyPanel.onImGuiRender();

        ImGui.begin("FPS");
        ImGui.text(Float.toString(ImGui.getIO().getFramerate()));
        ImGui.end();
        dockSpaceEnd();
    }

    private void dockSpaceBegin(){
        int dockWindowFlags = ImGuiWindowFlags.MenuBar | ImGuiWindowFlags.NoDocking | ImGuiWindowFlags.NoTitleBar
                | ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoMove
                | ImGuiWindowFlags.NoBringToFrontOnFocus | ImGuiWindowFlags.NoNavFocus;
        int dockNodeFlags = ImGuiDockNodeFlags.None;

        final ImGuiViewport viewport = ImGui.getMainViewport();
        ImGui.setNextWindowPos(viewport.getWorkPos().x, viewport.getWorkPos().y);
        ImGui.setNextWindowSize(viewport.getWorkSize().x, viewport.getWorkSize().y);
        ImGui.setNextWindowViewport(viewport.getID());
        ImGui.pushStyleVar(ImGuiStyleVar.WindowRounding, 0.0f);
        ImGui.pushStyleVar(ImGuiStyleVar.WindowBorderSize, 0.0f);

        // wesley: proceed even if Dock is collapse because we want to keep our dockspace active
        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 0.0f, 0.0f);
        ImGui.begin("DockSpace", dockWindowFlags);
        ImGui.popStyleVar();
        ImGui.popStyleVar(2);

        ImGuiIO io = ImGui.getIO();
        if((io.getConfigFlags() & ImGuiConfigFlags.DockingEnable) == 0){
            throw new IllegalStateException("Docking is not enabled!");
        }

        // wesley: Submit the DockSpace
        int dockSpaceID = ImGui.getID("WesDockSpace");
        ImGui.dockSpace(dockSpaceID, 0.0f, 0.0f, dockNodeFlags);
    }

    private void dockSpaceEnd(){
        ImGui.end();
    }
}
