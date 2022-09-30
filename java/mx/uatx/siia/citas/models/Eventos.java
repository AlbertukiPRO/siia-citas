package mx.uatx.siia.citas.models;

import net.bootsfaces.component.fullCalendar.FullCalendarEventBean;

import java.util.Arrays;
import java.util.Date;

public class Eventos extends FullCalendarEventBean {

    @Override
    public void setUrl(String url) {
        super.setUrl(url);
    }

    public Eventos(String title, Date start, String[] params) {
        super(title, start);

        setUrl("http://localhost:8081/SIIA/vistas/administracionCitas/viewCita.uat?idarea="+params[2]+"&idcita="+params[0]+"&matricula="+params[1]);
    }

    @Override
    public void addExtendedFields(StringBuilder buf) {

    }
}
