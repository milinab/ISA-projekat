package com.bank.Blood.Bank.model;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.*;

/*
 * @Entity anotacija naznacava da je klasa perzistentni entitet. Klasa ima konstruktor bez parametara.
 * Dodatno se moze iskoristiti anotacija @Table("naziv_tabele_u_bazi") kojom se
 * specificira tacan naziv tabele u bazi, sema kojoj pripada, itd. Ako se izostavi ova anotacija, dovoljno je
 * imati anotaciju @Entity i u bazi ce se kreirati tabela sa nazivom klase.
 */

/*
 * Efikasnost asocijacija:
 *
 * One-To-One:
 * - Unidirekcione/bidirekcione @OneToOne veze sa @MapsId su efikasne
 * - Bidirekcione @OneToOne bez @MapsId su manje efikasne
 *
 * One-To-Many:
 * - Bidirekcione @OneToMany i unidirekcione @ManyToOne su efikasne
 * - Unidirekcione @OneToMany sa Set kolekcijom su manje efikasne
 * - Unidirekcione @OneToMany sa List kolekcijom su prilično neefikasne
 *
 * Many-To-Many:
 * - Unidirekcione/bidirekcione @ManyToMany sa Set kolekcijom su efikasne
 * - Unidirekcione/bidirekcione @ManyToMany sa List kolekcijom su prilično neefikasne
 */
@Entity
@Getter
@Setter
@NoArgsConstructor

public class AppUser {

    /*
     * Svaki entitet ima svoj kljuc (surogat kljuc), dok se strategija generisanja
     * kljuceva moze eksplicitno podesiti: - AUTO - generisanje kljuceva se oslanja
     * na perzistencionog provajdera da izabere nacin generisanja (ako je u pitanju
     * Hibernate, selektuje tip na osnovu dijalekta baze, za najpopularnije baze
     * izabrace IDENTITY) - IDENTITY - inkrementalno generisanje kljuceva pri svakom
     * novom insertu u bazu - SEQUENCE - koriste se specijalni objekti baze da se
     * generisu id-evi - TABLE - postoji posebna tabela koja vodi racuna o
     * kljucevima svake tabele Vise informacija na:
     * https://en.wikibooks.org/wiki/Java_Persistence/Identity_and_Sequencing
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /*
     * Kolona moze imati ime koje se razlikuje od naziva atributa.
     */
    @Column(name = "username", unique = true, nullable = false)
    private String username;

    @Column(name = "password", unique = false, nullable = false)
    private String password;

    @Column
    private String email;



    @Column(name = "firstName", nullable = false)
    private String firstName;

    @Column(name = "lastName", nullable = false)
    private String lastName;

    /*
     * Primer bidirekcione veze 1:n. Student sadrzi kolekciju ispita, ispit pripada
     * jednom student. Jedna strana veze je anotirana sa @OneToMany, dok je druga
     * anotirana sa @ManyToOne. Dodatno je iskoriscen atribut mappedBy da se naznaci
     * ko je vlasnik veze (student). U bazi ce se u tabeli Exam kreirati dodatna
     * kolona koja ce sadrzati id objekata tipa Student kao strani kljuc. Ako se
     * izostavi mappedBy kreirace se medjutabela koja ce sadrzati 2 kolone - id
     * studenta i id ispita
     *
     * Atributom fetch moze se podesavati nacin dobavljanja povezanih entiteta.
     * Opcije su EAGER i LAZY. FetchType odlucuje da li ce se ucitati i sve veze sa
     * odgovarajucim objektom cim se inicijalno ucita sam objekat ili nece. Ako je
     * FetchType EAGER ucitace se sve veze sa objektom odmah, a ako je FetchType
     * LAZY ucitace se tek pri eksplicitnom trazenju povezanih objekata (pozivanjem
     * npr. metode getExams). Vise informacija na:
     * https://howtoprogramwithjava.com/hibernate-eager-vs-lazy-fetch-type/
     *
     * Pored atributa fetch moze se iskoristiti i atribut cascade. CascadeType
     * podesen na All dozvoljava da se prilikom svakog cuvanja, izmene ili brisanja
     * studenta cuvaju, menjaju ili brisu i ispiti. To znaci da ne moraju unapred da
     * se cuvaju ispiti pa onda povezuju sa studentom. orphanRemoval podesen na true
     * ce obezbediti da se ispiti izbrisu iz baze kada se izbrisu iz kolekcije
     * ispita u objektu student.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "address_id")
    private Address address;

    public AppUser(Integer id, String username, String password, String email, String firstName, String lastName, Address address) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AppUser t = (AppUser) o;
        return id != null && id.equals(t.getId());
    }

    @Override
    public int hashCode() {
        /*
         * Pretpostavka je da je u pitanju tranzijentni objekat (jos nije sacuvan u bazu) i da id ima null vrednost.
         * Kada se sacuva u bazu dobice non-null vrednost. To znaci da ce objekat imati razlicite kljuceve u dva stanja, te ce za generisan
         * hashCode i equals vratiti razlicite vrednosti. Vracanje konstantne vrednosti resava ovaj problem.
         * Sa druge strane ovakva implementacija moze da afektuje performanse u slucaju velikog broja objekata
         * koji ce zavrsiti u istom hash bucket-u.
         */
        return 1337;
    }


    /*
     * Pri implementaciji equals and hashCode metoda treba obratiti paznju da se
     * one razlikuju kada se koristi ORM (Hibernate) i kada se klase posmatraju kao
     * obicne POJO klase. Hibernate zahteva da entitet mora biti jednak samom sebi kroz sva
     * stanja tog objekta (tranzijentni (novi objekat), perzistentan (persistent), otkacen (detached) i obrisan (removed)).
     * To znaci da bi dobar pristup bio da se za generisanje equals i hashCode metoda koristi podatak
     * koji je jedinstven a poznat unapred (tzv. business key) npr. index studenta, isbn knjige, itd.
     * U slucaju da takvog obelezja nema, obicno se implementacija svodi na proveri da li je id (koji je kljuc) isti.
     * Posto u velikom broju slucajeva id baza generise, to znaci da u tranzijentnom stanju objekti nisu jednaki.
     * Postoji nekoliko resenja za ovaj problem:
     * 1. Naci neko jedinstveno obelezje
     * 2. Koristiti prirodne kljuceve
     * 3. Pre cuvanja na neki nacin saznati koja je sledeca vrednost koju ce baza generisati pa pozvati setId metodu da se kompletira objekat cak i pre cuvanja
     * 4. Na drugi nacin implementirati equals i hashCode - primer u klasi Teacher
     */

}