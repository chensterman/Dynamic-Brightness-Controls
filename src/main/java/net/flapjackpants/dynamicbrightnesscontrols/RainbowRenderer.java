package net.flapjackpants.dynamicbrightnesscontrols;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Matrix4f;

/**
 * Renders a rainbow overlay on the screen with configurable intensity.
 */
public class RainbowRenderer {
    private static final MinecraftClient client = MinecraftClient.getInstance();
    private static float intensity = 0.0f;
    private static long lastUpdateTime = System.currentTimeMillis();
    private static float hueOffset = 0.0f;

    /**
     * Sets the intensity of the rainbow effect.
     * @param newIntensity Value between 0.0 (no effect) and 1.0 (full effect)
     */
    public static void setIntensity(float newIntensity) {
        intensity = Math.max(0.0f, Math.min(1.0f, newIntensity));
    }

    /**
     * Renders the rainbow overlay on the screen.
     * @param matrices The matrix stack
     */
    public static void render(MatrixStack matrices) {
        if (intensity <= 0.01f) return; // Skip rendering if intensity is too low
        
        // Update the hue offset based on time for animation
        long currentTime = System.currentTimeMillis();
        float deltaTime = (currentTime - lastUpdateTime) / 1000.0f;
        lastUpdateTime = currentTime;
        hueOffset = (hueOffset + deltaTime * 0.2f) % 1.0f;
        
        int width = client.getWindow().getScaledWidth();
        int height = client.getWindow().getScaledHeight();
        
        // Setup rendering
        RenderSystem.disableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        
        // Draw rainbow overlay
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        
        Matrix4f matrix = matrices.peek().getPositionMatrix();
        
        // Draw rainbow gradient across the screen
        for (int i = 0; i < width; i += width / 6) {
            float hue1 = (i / (float)width + hueOffset) % 1.0f;
            float hue2 = ((i + width / 6) / (float)width + hueOffset) % 1.0f;
            
            int color1 = hsvToRgb(hue1, 1.0f, 1.0f);
            int color2 = hsvToRgb(hue2, 1.0f, 1.0f);
            
            int r1 = (color1 >> 16) & 0xFF;
            int g1 = (color1 >> 8) & 0xFF;
            int b1 = color1 & 0xFF;
            
            int r2 = (color2 >> 16) & 0xFF;
            int g2 = (color2 >> 8) & 0xFF;
            int b2 = color2 & 0xFF;
            
            // Apply intensity to alpha
            int alpha = (int)(intensity * 100);
            
            bufferBuilder.vertex(matrix, i, 0, 0).color(r1, g1, b1, alpha).next();
            bufferBuilder.vertex(matrix, i + width / 6, 0, 0).color(r2, g2, b2, alpha).next();
            bufferBuilder.vertex(matrix, i + width / 6, height, 0).color(r2, g2, b2, alpha).next();
            bufferBuilder.vertex(matrix, i, height, 0).color(r1, g1, b1, alpha).next();
        }
        
        BufferRenderer.drawWithShader(bufferBuilder.end());
        
        // Cleanup
        RenderSystem.disableBlend();
        RenderSystem.enableDepthTest();
    }
    
    /**
     * Converts HSV color to RGB.
     * @param h Hue (0.0-1.0)
     * @param s Saturation (0.0-1.0)
     * @param v Value (0.0-1.0)
     * @return RGB color as an integer
     */
    private static int hsvToRgb(float h, float s, float v) {
        float c = v * s;
        float x = c * (1 - Math.abs((h * 6) % 2 - 1));
        float m = v - c;
        
        float r, g, b;
        if (h < 1.0f/6.0f) {
            r = c; g = x; b = 0;
        } else if (h < 2.0f/6.0f) {
            r = x; g = c; b = 0;
        } else if (h < 3.0f/6.0f) {
            r = 0; g = c; b = x;
        } else if (h < 4.0f/6.0f) {
            r = 0; g = x; b = c;
        } else if (h < 5.0f/6.0f) {
            r = x; g = 0; b = c;
        } else {
            r = c; g = 0; b = x;
        }
        
        int red = (int)((r + m) * 255);
        int green = (int)((g + m) * 255);
        int blue = (int)((b + m) * 255);
        
        return (red << 16) | (green << 8) | blue;
    }
}
