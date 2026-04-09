package compiler;


import javax.tools.*;
import java.io.*;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class MemoryJavaFileManager extends ForwardingJavaFileManager<JavaFileManager> {
    private final Map<String, ByteArrayOutputStream> classBytes = new HashMap<>();
    
    public MemoryJavaFileManager(JavaFileManager fileManager) {
        super(fileManager);
    }
    
    @Override
    public JavaFileObject getJavaFileForOutput(Location location, String className,
                                               JavaFileObject.Kind kind, FileObject sibling) {
        return new SimpleJavaFileObject(URI.create("mem:///" + className.replace('.', '/') + kind.extension), kind) {
            @Override
            public OutputStream openOutputStream() {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                classBytes.put(className, baos);
                return baos;
            }
        };
    }
    
    public Map<String, byte[]> getClassBytes() {
        Map<String, byte[]> result = new HashMap<>();
        for (Map.Entry<String, ByteArrayOutputStream> entry : classBytes.entrySet()) {
            result.put(entry.getKey(), entry.getValue().toByteArray());
        }
        return result;
    }
}
