package jeronimo.margitic.model;

public enum EstadoObra {
    /* una obra está habilitada si no sobrepasa el máximo de cantidad de obras activas. 
    Caso contrario se da de alta como Pendiente para despachos y envíos */
    HABILITADA,

    /* : la obra no está habilitada porque el cliente alcanzó el máximo permitido */
    PENDIENTE,

    /* la obra está finalizada y no cuenta en las obras habilitadas */
    FINALIZADA
}
