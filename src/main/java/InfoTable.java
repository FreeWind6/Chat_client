import javafx.beans.property.SimpleStringProperty;

public class InfoTable {

    private SimpleStringProperty command;
    private SimpleStringProperty description;
    private SimpleStringProperty example;

    public InfoTable(String command, String description, String example) {
        this.command = new SimpleStringProperty(command);
        this.description = new SimpleStringProperty(description);
        this.example = new SimpleStringProperty(example);
    }

    public String getCommand() {
        return command.get();
    }

    public SimpleStringProperty commandProperty() {
        return command;
    }

    public void setCommand(String command) {
        this.command.set(command);
    }

    public String getDescription() {
        return description.get();
    }

    public SimpleStringProperty descriptionProperty() {
        return description;
    }

    public void setDescription(String description) {
        this.description.set(description);
    }

    public String getExample() {
        return example.get();
    }

    public SimpleStringProperty exampleProperty() {
        return example;
    }

    public void setExample(String example) {
        this.example.set(example);
    }
}
