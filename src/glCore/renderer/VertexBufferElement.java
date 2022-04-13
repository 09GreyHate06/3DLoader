package glCore.renderer;

public class VertexBufferElement {
    public int attribIndex = 0;
    public int count = 0;
    public boolean normalized = false;
    public int offset = 0;

    public VertexBufferElement(int attribIndex, int count, boolean normalized, int offset){
        this.attribIndex = attribIndex;
        this.count = count;
        this.normalized = normalized;
        this.offset = offset;
    }
}

