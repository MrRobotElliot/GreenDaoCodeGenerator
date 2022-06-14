package plugin.elliot.greendaocodegenerator.tools.ux;

public interface CellProvider {

    String getCellTitle(int index);

    void setValueAt(int column, String text);
}
