package Controller;
import Model.Entities.Funcionarios.Vigilante;
import Model.Entities.Mensalista.Mensalista;
import Model.Entities.Ticket.TicketCliente;
import Model.Entities.Ticket.TicketMensalista;
import Model.Entities.Vagas.Vagas;
import Model.UseCases.MensalistaUseCase;
import Model.UseCases.RegistroVigilanteUseCase;
import Model.UseCases.TicketUseCase;
import Model.UseCases.VagasUseCase;
import Utils.MaskFieldUtil;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class WindowTicketController {
    @FXML
    Label lblErro;

    @FXML
    JFXButton btnCancelaTicket;

    @FXML
    JFXButton btnGerarTicket;

    @FXML
    JFXTextArea taDescCarroCliente;

    @FXML
    JFXTextField tfContatoCliente;

    @FXML
    JFXTextField tfPlacaCliente;

    @FXML
    JFXCheckBox cbTaxaPerNoite;

    @FXML
    JFXTextField tfCpfCliente;

    @FXML
    JFXTextField tfVagaOcupada;

    @FXML
    TableView<Vagas> vagasDisponiveisTable;

    @FXML
    TableColumn<Vagas, Integer> vagasDisponiveisColum;

    private ObservableList<Vagas> values;
    private TicketUseCase ticketUseCase = new TicketUseCase();
    private RegistroVigilanteUseCase registroVigilanteUseCase = new RegistroVigilanteUseCase();
    private MensalistaUseCase mensalistaUseCase = new MensalistaUseCase();

    public void geraTicket(ActionEvent actionEvent) throws SQLException {
        Vigilante vigilante = registroVigilanteUseCase.getCurrentVigilante();
        if(vigilante!=null) {
            TicketCliente ticketCliente = new TicketCliente(
                    tfPlacaCliente.getText(),
                    new Date(),
                    null,
                    taDescCarroCliente.getText(),
                    vigilante.getId());
            ticketCliente.setTelefone(tfContatoCliente.getText());
            if (cbTaxaPerNoite.isSelected()) {
                ticketCliente.setPernoite(true);
            } else {
                ticketCliente.setPernoite(false);
            }
            ticketCliente.setCpf(tfCpfCliente.getText());
            ticketUseCase.saveClientTicket(ticketCliente);
            /*Salvando a vaga*/
            VagasUseCase vagasUseCase = new VagasUseCase();
            vagasUseCase.setVaga(tfCpfCliente.getText(), Integer.parseInt(tfVagaOcupada.getText()));
            /*Fecha a aba*/
            ((Stage) btnGerarTicket.getScene().getWindow()).close();
        }   else{
            lblErro.setText("Nenhum vigilante em trabalho");
        }
    }

    public void cancelaTicket(ActionEvent actionEvent) {
        Stage stage = (Stage) btnCancelaTicket.getScene().getWindow();
        stage.close();
    }

    public void formatterCpfClient(KeyEvent keyEvent) {
        MaskFieldUtil.cpfField(tfCpfCliente);
    }
    public void formatterTellClient(KeyEvent keyEvent) {
        MaskFieldUtil.foneField(tfContatoCliente);
    }

    @FXML
    private void initialize() throws SQLException {
        Date date = new Date();   // given date
        Calendar calendar = GregorianCalendar.getInstance(); // creates a new calendar instance
        calendar.setTime(date);
        if(calendar.get(Calendar.HOUR_OF_DAY) <= 20 && calendar.get(Calendar.HOUR_OF_DAY) >= 8){
            cbTaxaPerNoite.setDisable(true);
        }
        vagasDisponiveisColum.setCellValueFactory(new PropertyValueFactory<>("id_vaga"));
        values = FXCollections.observableArrayList();
        vagasDisponiveisTable.setItems(values);
        loadTableView();
    }

    private void loadTableView() throws SQLException {
        VagasUseCase vagasUseCase = new VagasUseCase();
        values.setAll(vagasUseCase.readAllLivres());
    }

    public void selecionaVaga(MouseEvent mouseEvent) {
        tfVagaOcupada.setText(String.valueOf(vagasDisponiveisTable.getSelectionModel().getSelectedItem().getId_vaga()));
    }
}