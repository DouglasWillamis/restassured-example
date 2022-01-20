package models;

import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;

final public class User {

    @Expose
    private String nome;
    @Expose
    private String email;
    @Expose
    private String password;
    private String _id;
    @Expose
    private String administrador;
    private String tokenAuthentication;

    public User(String nome, String email, String password, String administrador) {
        this.nome = nome;
        this.email = email;
        this.password = password;
        this.administrador = administrador;
    }

    public User(String nome, String email, String password, String administrador, String _id) {
        this.nome = nome;
        this.email = email;
        this.password = password;
        this.administrador = administrador;
        this._id = _id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getAdministrador() {
        return administrador;
    }

    public void setAdministrador(String administrador) {
        this.administrador = administrador;
    }

    public String getTokenAuthentication() {
        return tokenAuthentication;
    }

    public void setTokenAuthentication(String tokenAuthentication) {
        this.tokenAuthentication = tokenAuthentication;
    }

    public String getCredencial() {
        JsonObject userJsonRepresentation = new JsonObject();

        userJsonRepresentation.addProperty("email", this.email);
        userJsonRepresentation.addProperty("password", this.password);

        return userJsonRepresentation.toString();
    }
}
