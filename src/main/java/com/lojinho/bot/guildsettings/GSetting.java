package com.lojinho.bot.guildsettings;

import java.util.Collections;
import java.util.HashSet;

import com.lojinho.bot.guildsettings.types.NoSettingType;
import com.lojinho.bot.guildsettings.types.StringLengthSettingType;

import net.dv8tion.jda.api.entities.Guild;

public enum GSetting {
  COMMAND_PREFIX("loj.", new StringLengthSettingType(1, 4),"Prefixo para utilizar os comandos (entre 1 e 4 caracteres)", GSettingTag.COMMAND, GSettingTag.MODERATION),
  LOG_CHANNEL_BOT_EVENTS("desabilitado", GuildSettingType.TEXT_CHANNEL_OPTIONAL,"O canal ocorre o log de eventos do bot.", GSettingTag.LOGGING, GSettingTag.CHANNEL, GSettingTag.INTERNAL),
  LOG_CHANNEL_MOD_ACTION("desabilitado", GuildSettingType.TEXT_CHANNEL_OPTIONAL,"O canal ocorre o log de ações moderativas do servidor.", GSettingTag.LOGGING, GSettingTag.CHANNEL),
  LOG_CHANNEL_USER_ACTION("desabilitado", GuildSettingType.TEXT_CHANNEL_OPTIONAL,"O canal ocorre o log de ações de usuário.", GSettingTag.LOGGING, GSettingTag.CHANNEL),
  LOG_CHANNEL_VOICE_ACTIVITY("desabilitado", GuildSettingType.TEXT_CHANNEL_OPTIONAL,"O canal ocorre o log de canais de voz.", GSettingTag.LOGGING, GSettingTag.CHANNEL),
  LOG_CHANNEL_ONJOIN("desabilitado", GuildSettingType.TEXT_CHANNEL_OPTIONAL,"O canal ocorre o log de entrada de usuários no servidor.", GSettingTag.LOGGING, GSettingTag.CHANNEL),
  LOG_CHANNEL_ONLEAVE("desabilitado", GuildSettingType.TEXT_CHANNEL_OPTIONAL,"O canal ocorre o log de saída de usuários do servidor.", GSettingTag.LOGGING, GSettingTag.CHANNEL),
  LOG_CHANNEL_COMMAND_USE("desabilitado", GuildSettingType.TEXT_CHANNEL_OPTIONAL,"O canal ocorre o log do uso de comandos no servidor.", GSettingTag.LOGGING, GSettingTag.CHANNEL),
  TESTE_COM_CONFIGURACAO_COM_NOME_DESNECESSARIAMENTE_GRANDE_PRA_VER_O_QUE_ACONTECE("desabilitado", new NoSettingType(),"Teste 1", GSettingTag.DEBUG),
  TEST_CFG_1("desabilitado", new NoSettingType(), "Teste 1", GSettingTag.DEBUG),
  TEST_CFG_2("desabilitado", new NoSettingType(), "Teste 2", GSettingTag.DEBUG),
  TEST_CFG_3("desabilitado", new NoSettingType(), "Teste 3", GSettingTag.DEBUG),;

  private final String defaultValue;
  private final IGuildSettingType settingType;
  private final String description;
  private final HashSet<GSettingTag> tags;

  GSetting(String defaultValue, IGuildSettingType settingType, String description, GSettingTag... tags) {
    this.defaultValue = defaultValue;
    this.settingType = settingType;
    this.description = description;
    this.tags = new HashSet<>();
    Collections.addAll(this.tags, tags);
  }

  /**
   * @return se o comando é Interno (bot config)
   */
  public boolean isInternal() {
    return tags.contains(GSettingTag.INTERNAL);
  }

  /**
   * @return o valor padrão desta GSetting
   */
  public String getDefaultValue() {
    return defaultValue;
  }

  /**
   * @return a descrição desta GSetting
   */
  public String getDescription() {
    return description;
  }

  /**
   * @param tag a tag a verificar se a GSetting possui
   * @return se esta GSetting possui a tag especificada
   */
  public boolean hasTag(String tag) {
    return tags.contains(GSettingTag.valueOf(tag.toUpperCase()));
  }

  /**
   * @param tag a tag a verificar se a GSetting possui
   * @return se esta GSetting possui a tag especificada
   */
  public boolean hasTag(GSettingTag tag) {
    return tags.contains(tag);
  }

  /**
   * @return as tags que esta GSetting possui
   */
  public HashSet<GSettingTag> getTags() {
    return tags;
  }

  /**
   * Verifica se o input é uma entrada válida para esta GSetting
   *
   * @param guild - utilitário para o caso de ser necessário na validação
   * @param input - valor a ser validado
   * @return se o input é válido
   */
  public boolean isValidValue(Guild guild, String input) {
    return settingType.validate(guild, input);
  }

  /**
   * Obtém o valor formatado de acordo com o tipo desta GSetting
   * 
   * @param guild - utilitário para o caso de ser necessário na formatação
   * @param input - valor a ser formatado
   * @return o valor desta GSetting formatado corretamente
   */
  public String getValue(Guild guild, String input) {
    return settingType.fromInput(guild, input);
  }

  /**
   * Obtém o valor formatado para exibição de acordo com o tipo desta GSetting
   * 
   * @param guild - utilitário para o caso de ser necessário na formatação
   * @param input - valor a ser formatado
   * @return o valor desta GSetting formatado para exibição corretamente
   */
  public String toDisplay(Guild guild, String value) {
    return settingType.toDisplay(guild, value);
  }

  /**
   * @return o tipo desta GSetting
   */
  public IGuildSettingType getSettingType() {
    return settingType;
  }
}