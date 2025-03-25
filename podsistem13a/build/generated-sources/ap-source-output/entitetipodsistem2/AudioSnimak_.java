package entitetipodsistem2;

import entitetipodsistem2.Pripada;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.7.12.v20230209-rNA", date="2024-12-18T20:29:54")
@StaticMetamodel(AudioSnimak.class)
public class AudioSnimak_ { 

    public static volatile ListAttribute<AudioSnimak, Pripada> pripadaList;
    public static volatile SingularAttribute<AudioSnimak, Integer> idK;
    public static volatile SingularAttribute<AudioSnimak, Date> datumPostavljen;
    public static volatile SingularAttribute<AudioSnimak, Integer> trajanje;
    public static volatile SingularAttribute<AudioSnimak, String> naziv;
    public static volatile SingularAttribute<AudioSnimak, Integer> id;

}