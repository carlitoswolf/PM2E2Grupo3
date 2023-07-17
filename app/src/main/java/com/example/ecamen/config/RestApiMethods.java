package com.example.ecamen.config;

public class RestApiMethods
{
    public static final String ipaddress = "192.168.8.214";
    public static final String webapi = "Examen-BackEnd";
    public static final String separador = "/";

    // Routing -- CRUD
    public static final String postRouting = "CreatePerson.php";
    public static final String getRouting = "ReadPeople.php";
    public static final String updRouting = "UpdatePerson.php";
    public static final String delRouting = "Delete.php";

    // Endpoint
    public static final String ApiPost = "http://"+ipaddress+ separador +webapi + separador +postRouting;
    public static final String ApiGet = "http://"+ipaddress+ separador +webapi + separador +getRouting;
    public static final String Apiupd = "http://"+ipaddress+ separador +webapi + separador +updRouting;
    public static final String Apidel = "http://"+ipaddress+ separador +webapi + separador +delRouting;

}
