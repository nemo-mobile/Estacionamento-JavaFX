package Model.UseCases;

import Model.Entities.Funcionarios.Vigilante;
import Utils.SqlConnection;
import javafx.css.converter.DurationConverter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.TimeZone;


public class AtualizaVigilanteUseCase {
    Connection connection;

    public AtualizaVigilanteUseCase(){
        Calendar calendar = Calendar.getInstance();
        connection = SqlConnection.getConnection();
        if(connection == null) System.exit(1);
    }

    public void atualizaVigilante(Vigilante newVig){
        Vigilante currentVigilante = getCurrentVigilante();
        String currentTimeStamp = getCurrentTimeStampFormatted();

        PreparedStatement preparedStatement = null;
        String sql;
        try{

            if (currentVigilante == null ){
                // INSERE NOVO VIGILANTE COMO VIGITALNTE ATUAL
                sql = "INSERT INTO last_vigilante  VALUES (?)";
                preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setInt(1, newVig.getId());
                preparedStatement.execute();

                //INSERE A HORA DE INICIO DO NOVO VIGILANTE
                sql = "INSERT INTO working_hours(funcionario_id, horario_entrada) VALUES (?, ?)";
                preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setInt(1, newVig.getId());
                preparedStatement.setString(2, currentTimeStamp);
                preparedStatement.execute();


            }

            if(currentVigilante != null  && currentVigilante.getId() != newVig.getId()){

                //INSERE HORARIO DE INICIO DO NOVO VIGILANTE
                sql = "INSERT INTO working_hours(funcionario_id, horario_entrada) VALUES (?, ?)";
                preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setInt(1, newVig.getId());
                preparedStatement.setString(2, currentTimeStamp);
                preparedStatement.execute();

                //INSERE O HORARIO DE SÁIDA  DO ANTIGO VIGILANTE
                sql = "UPDATE working_hours SET horario_saida = ? WHERE id = (select max(id) from working_hours where funcionario_id = ?)";

                preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setString(1, currentTimeStamp);
                preparedStatement.setInt(2, currentVigilante.getId());
                preparedStatement.execute();



                //INSERE AS HORAS TRABALHADAS DO VIGILANTE ANTIGO
                sql = "UPDATE working_hours SET horas_trabalhadas = ? WHERE id = (select max(id) from working_hours where funcionario_id = ?) ";
                String horasTrabalhadas =  getWorkedHours(currentVigilante.getId());
                preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setString(1, horasTrabalhadas);
                preparedStatement.setInt(2, currentVigilante.getId());
                preparedStatement.execute();

                //ATUALIZA TABELA VIGILANTE ATUAL COM ID DO NOVO VIGILANTE
                sql = "UPDATE last_vigilante SET id_vigilante = ?";
                preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setInt(1, newVig.getId());
                preparedStatement.execute();

            }

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public Vigilante getCurrentVigilante(){
        try {
            Vigilante currentVigilante = null;

            String sql = "SELECT id_vigilante FROM last_vigilante";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();

            if(resultSet.next()){
               int idVigilante = resultSet.getInt(1);
               FuncionarioUseCase funcionarioUseCase = new FuncionarioUseCase();
               return (Vigilante) funcionarioUseCase.read(idVigilante);
            }

        }
        catch (Exception e){
            e.printStackTrace();
        }

        return  null;
    }

    private String getWorkedHours(int id){
        try {
            String sql = "SELECT horario_entrada, horario_saida FROM working_hours  WHERE id = (select max(id) from working_hours where funcionario_id = ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();

            if(resultSet.next()){
                LocalDateTime horarioEntrada = convertStringToLocalTime(resultSet.getString(1));
                LocalDateTime horarioSaida = convertStringToLocalTime(resultSet.getString(2));
                Duration diferencaDuration = Duration.between(horarioSaida, horarioEntrada);
                int horas = Math.abs(diferencaDuration.toHoursPart());
                int minutos = Math.abs(diferencaDuration.toMinutesPart());
                int segundos = Math.abs(diferencaDuration.toSecondsPart());

                return  horas + ":" + minutos + ":" + segundos;
            }

        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    private  LocalDateTime convertStringToLocalTime(String date){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        return LocalDateTime.parse(date, formatter);
    }

    private String getCurrentTimeStampFormatted(){
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        System.out.println(now.format(formatter));
        return now.format(formatter);

    }

}
