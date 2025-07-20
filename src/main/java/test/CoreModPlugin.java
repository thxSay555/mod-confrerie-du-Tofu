// src/main/java/fr/wakfu/coremod/CoreModPlugin.java
package test;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import java.util.Map;

@IFMLLoadingPlugin.Name("Wakfu ItemAnim CoreMod")
@IFMLLoadingPlugin.MCVersion("1.12.2")
public class CoreModPlugin implements IFMLLoadingPlugin {
    @Override
    public String[] getASMTransformerClass() {
        // Le nom complet de ton transformer
        return new String[] { "test.ItemRendererTransformer" };
    }
    @Override public String getModContainerClass()  { return null; }
    @Override public String getSetupClass()       { return null; }
    @Override public void injectData(Map<String, Object> data) {}
    @Override public String getAccessTransformerClass() { return null; }
}
