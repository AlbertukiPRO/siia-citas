package mx.uatx.siia.citas;

import mx.uatx.siia.comun.helper.VistasHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.annotation.SessionScope;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;

@SessionScope
@ManagedBean(name = "admin")
public class AdminCitasBean implements Serializable {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -2621529818431646329L;
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @ManagedProperty("#{msj}")
    private ResourceBundle msj;

    private final VistasHelper vHelp = new VistasHelper();

    private String anoActual;
    private List<String> listMeses;

    public int getToday() {
        return today;
    }

    public void setToday(int today) {
        this.today = today;
    }

    private List<String> listDias;
    private int today;
    private String mesActual;
    private String strDia;

    public AdminCitasBean(){
        listMeses = Arrays.asList("Enero", "Febrero", "Marzo","Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre","Octubre", "Noviembre", "Diciembre");
        GenerarFechas(4);
        LocalDate todaylocal = LocalDate.now();
        today = todaylocal.getDayOfMonth();
        mesActual = GetIndexMes(todaylocal.getMonthValue()-1);

        System.out.println("--- Today => "+today);
        System.out.println("--- Month => "+mesActual);

        GetCitasFromDia(today);

        getDayString();
    }

    private void getDayString(){

        try {
            SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
            Date dt1=format.parse("05/26/2022");
            DateFormat format2=new SimpleDateFormat("EEEE");
            String finalDay=format2.format(dt1);
            System.out.println(finalDay);

            this.strDia = finalDay;
        }catch (Exception e){
            System.out.println(e);
        }


    }

    public void GenerarFechas(int mes){
        listDias = new ArrayList<>();
        YearMonth yearMonth = YearMonth.of(2022,mes+1);
        int daysInMonth = yearMonth.lengthOfMonth();

        for (int i = 1; i <= daysInMonth; i++) {
            listDias.add(Integer.toString(i));
        }
        System.out.println("--- FECHAS GENERADAS : "+listDias);
    }

    public void GetCitasFromDia(int dia){

        System.out.println("--GET CITAS FROM DIA");



    }

    public void RefrescarFechas(String mes){
        System.out.println("--- SEND "+mes+" To Generate Function");
        switch (mes){
            case "Enero":
                GenerarFechas(0);
                break;
            case "Febrero":
                GenerarFechas(1);
                break;
            case "Marzo":
                GenerarFechas(2);
                break;
            case "Abril":
                GenerarFechas(3);
                break;
            case "Mayo":
                GenerarFechas(4);
                break;
            case "Junio":
                GenerarFechas(5);
                break;
            case "Julio":
                GenerarFechas(6);
                break;
            case "Agosto":
                GenerarFechas(7);
                break;
            case "Septiembre":
                GenerarFechas(8);
                break;
            case "Octubre":
                GenerarFechas(9);
                break;
            case "Noviembre":
                GenerarFechas(10);
                break;
            case "Diciembre":
                GenerarFechas(11);
                break;
        }
    }


    public String GetIndexMes(int mesString){
        String mesCurrent = "";
        switch (mesString){
            case 0:
                mesCurrent = "Enero";
                break;
            case 1:
                mesCurrent = "Febrero";
                break;
            case 2:
                mesCurrent = "Marzo";
                break;
            case 3:
                mesCurrent = "Abril";
                break;
            case 4:
                mesCurrent = "Mayo";
                break;
            case 5:
                mesCurrent = "Junio";
                break;
            case 6:
                mesCurrent = "Julio";
                break;
            case 7:
                mesCurrent = "Agosto";
                break;
            case 8:
                mesCurrent = "Septiembre";
                break;
            case 9:
                mesCurrent = "Octubre";
                break;
            case 10:
                mesCurrent = "Noviembre";
                break;
            case 11:
                mesCurrent = "Diciembre";
                break;
        }
        return mesCurrent;
    }
    public Logger getLogger() {
        return logger;
    }
    public List<String> getListMeses() {
        return listMeses;
    }
    public String getMesActual() {
        return mesActual;
    }

    public void setMesActual(String mesActual) {
        this.mesActual = mesActual;
    }
    public String getStrDia() {
        return strDia;
    }

    public void setStrDia(String strDia) {
        this.strDia = strDia;
    }



    public List<String> getListDias() {
        return listDias;
    }

    public void setListDias(List<String> listDias) {
        this.listDias = listDias;
    }


    public void setListMeses(List<String> listMeses) {
        this.listMeses = listMeses;
    }

    public ResourceBundle getMsj() {
        return msj;
    }

    public void setMsj(ResourceBundle msj) {
        this.msj = msj;
    }

    public String getAnoActual() {
        return anoActual;
    }

    public void setAnoActual(String anoActual) {
        this.anoActual = anoActual;
    }

}
