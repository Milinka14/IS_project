package entiteti;

import entiteti.Pretplata;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.7.12.v20230209-rNA", date="2024-12-18T20:29:54")
@StaticMetamodel(Paket.class)
public class Paket_ { 

    public static volatile SingularAttribute<Paket, Integer> trenutnaCijenaPaketa;
    public static volatile ListAttribute<Paket, Pretplata> pretplataList;
    public static volatile SingularAttribute<Paket, String> naziv;
    public static volatile SingularAttribute<Paket, Integer> id;

}