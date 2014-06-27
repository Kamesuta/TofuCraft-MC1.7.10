package tsuteto.tofu.gui;

public class GuiPartGuageRevH extends GuiPartGuageH<GuiPartGuageRevH>
{
    public GuiPartGuageRevH(int x, int y, TfMachineGuiParts frame, TfMachineGuiParts indicator)
    {
        super(x, y, frame, indicator);
    }

    public GuiPartGuageRevH(int x, int y, TfMachineGuiParts frame, TfMachineGuiParts indicator, int xIndicatorOffset, int yIndicatorOffset)
    {
        super(x, y, frame, indicator, xIndicatorOffset, yIndicatorOffset);
    }

    @Override
    protected void drawIndicator(GuiTfMachineBase gui)
    {
        int xSize = (int)(indicator.xSize * percentage);
        gui.drawTexturedModalRect(
                gui.getGuiLeft() + x + (indicator.xSize - xSize) + xIndicatorOffset, gui.getGuiTop() + y + yIndicatorOffset,
                indicator.xSize - xSize + indicator.ox, indicator.oy,
                xSize, indicator.ySize,
                indicatorColor);
    }
}
