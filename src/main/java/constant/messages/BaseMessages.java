package constant.messages;

public interface BaseMessages {
    String entityCreatedMessage = "Cadastro realizado com sucesso";
    String entityUpdateMessage = "Registro alterado com sucesso";
    String entityDeleteMessage = "Registro excluído com sucesso";
    String entityNotFoundDeleteMessage = "Nenhum registro excluído";
    String requestWithoutToken = "Token de acesso ausente, inválido, expirado ou usuário do token não existe mais";
}
