/**
 * WARNING!  THIS FILE IS AUTOMATICALLY GENERATED!  DO NOT MODIFY IT!
 * Generated on Wed Mar 08 13:02:33 EST 2017
 */

package com.ibm.ws.logging.hpel.viewer.internal.resources;

public class BinaryLogMessages_pt_BR extends java.util.ListResourceBundle
{
   public Object[][] getContents() {
       return resources;
   }
   private final static Object[][] resources= {
      { "BL_BAD_FORMAT", "O formato {0} n\u00e3o \u00e9 reconhecido." },
      { "BL_COPY_REQUIRES_TARGETDIR", "A a\u00e7\u00e3o de c\u00f3pia requer que um {serverName | repositoryPath} e um targetPath sejam especificados." },
      { "BL_COPY_USAGE_001", "Uso: binaryLog copy {serverName | repositoryPath} targetPath [options]" },
      { "BL_COPY_USAGE_002", "    targetPath" },
      { "BL_COPY_USAGE_003", "\tEspecifique o caminho no qual criar um novo reposit\u00f3rio." },
      { "BL_COPY_USAGE_004", "\tLeia um reposit\u00f3rio, filtre-o, opcionalmente, e grave o conte\u00fado em um \n\tnovo reposit\u00f3rio." },
      { "BL_INVALID_ACTION", "A a\u00e7\u00e3o especificada {0} n\u00e3o \u00e9 v\u00e1lida.  " },
      { "BL_INVALID_MAXDATE", "N\u00e3o \u00e9 poss\u00edvel analisar o valor --maxDate." },
      { "BL_INVALID_MINDATE", "N\u00e3o \u00e9 poss\u00edvel analisar o valor --minDate." },
      { "BL_INVALID_REPOSITORYDIR", "O caminho do reposit\u00f3rio {0} n\u00e3o \u00e9 um nome de caminho v\u00e1lido." },
      { "BL_INVALID_TARGETDIR", "O caminho de destino {0} n\u00e3o \u00e9 um nome de caminho v\u00e1lido." },
      { "BL_LISTINSTANCES_USAGE_001", "Uso: binaryLog listInstances {serverName | repositoryPath} [options]" },
      { "BL_LISTINSTANCES_USAGE_002", "\tListe os IDs de inst\u00e2ncias do servidor no reposit\u00f3rio.  Uma inst\u00e2ncia        \n\tde servidor \u00e9 a cole\u00e7\u00e3o de todos os registros de log/rastreio gravados do \n\tmomento que um servidor \u00e9 iniciado at\u00e9 sua parada.  IDs de inst\u00e2ncias do servidor   \n\tpodem ser usados com a op\u00e7\u00e3o --includeInstance da a\u00e7\u00e3o  \n\tbinaryLog view." },
      { "BL_MAIN_USAGE_001", "Uso: binaryLog action {serverName | repositoryPath} [options]" },
      { "BL_MAIN_USAGE_004", "    serverName" },
      { "BL_MAIN_USAGE_005", "\tEspecifique o nome de um servidor Liberty com um reposit\u00f3rio do qual ler." },
      { "BL_MAIN_USAGE_006", "    repositoryPath" },
      { "BL_MAIN_USAGE_007", "\tEspecifique o caminho para um reposit\u00f3rio do qual ler.  Esse \u00e9 geralmente o\n\tdiret\u00f3rio que cont\u00e9m os diret\u00f3rios logdata e tracedata." },
      { "BL_MAIN_USAGE_008", "Descri\u00e7\u00e3o:" },
      { "BL_MAIN_USAGE_009", "\tVisualize ou copie o conte\u00fado de um reposit\u00f3rio de Cria\u00e7\u00e3o de Log Extens\u00edvel de   \n\tAlto Desempenho ou liste as inst\u00e2ncias de processo do servidor dispon\u00edveis no    \n\treposit\u00f3rio." },
      { "BL_MAIN_USAGE_010", "A\u00e7\u00f5es:" },
      { "BL_MAIN_USAGE_011", "    view" },
      { "BL_MAIN_USAGE_012", "\tLeia um reposit\u00f3rio, filtre-o, opcionalmente, e crie uma vers\u00e3o \n\tleg\u00edvel." },
      { "BL_MAIN_USAGE_013", "    copy" },
      { "BL_MAIN_USAGE_014", "\tLeia um reposit\u00f3rio, filtre-o, opcionalmente, e grave o conte\u00fado em um \n\tnovo reposit\u00f3rio." },
      { "BL_MAIN_USAGE_015", "    listInstances" },
      { "BL_MAIN_USAGE_016", "\tListe as inst\u00e2ncias de processo do servidor no reposit\u00f3rio." },
      { "BL_MAIN_USAGE_017", "Op\u00e7\u00f5es:" },
      { "BL_MAIN_USAGE_018", "\tUse help [action] para obter informa\u00e7\u00f5es de op\u00e7\u00e3o detalhadas de cada a\u00e7\u00e3o." },
      { "BL_MINDATE_AFTER_MAXDATE", "A data especificada por --minDate \u00e9 posterior \u00e0 data especificada por --maxDate." },
      { "BL_MINLEVEL_GREATER_THAN_MAXLEVEL", "O n\u00edvel especificado por --minLevel \u00e9 superior ao n\u00edvel especificado por --maxLevel." },
      { "BL_NO_FILES_FOUND", "O diret\u00f3rio de logs ou o diret\u00f3rio de reposit\u00f3rio do servidor especificado n\u00e3o cont\u00e9m arquivos de log ou de rastreio." },
      { "BL_NO_FILES_FOUND_MONITOR", "O diret\u00f3rio especificado n\u00e3o cont\u00e9m atualmente arquivos de log ou de rastreio.  Continuando a monitorar esse diret\u00f3rio." },
      { "BL_OPTION_REQUIRES_A_VALUE", "A op\u00e7\u00e3o {0} requer um valor." },
      { "BL_REPOSITORY_DIRECTORY", "Usando {0} como um diret\u00f3rio de reposit\u00f3rio. " },
      { "BL_TARGET_DIRECTORY", "Usando {0} como um diret\u00f3rio de destino." },
      { "BL_UNABLE_TO_COPY", "N\u00e3o \u00e9 poss\u00edvel criar um reposit\u00f3rio no local de destino. Assegure-se de que o diret\u00f3rio de destino especificado esteja vazio e de que tenha permiss\u00f5es de grava\u00e7\u00e3o." },
      { "BL_UNKNOWN_OPTION", "A op\u00e7\u00e3o {0} n\u00e3o \u00e9 reconhecida." },
      { "BL_USE_HELP", "Para obter informa\u00e7\u00f5es de uso, use binaryLog help." },
      { "BL_VIEW_USAGE_001", "Uso: binaryLog view {serverName | repositoryPath} [options]" },
      { "BL_VIEW_USAGE_002", "\tLeia um reposit\u00f3rio, filtre-o, opcionalmente, e crie uma vers\u00e3o \n\tleg\u00edvel." },
      { "BL_VIEW_USAGE_003", "Op\u00e7\u00f5es de filtro:" },
      { "BL_VIEW_USAGE_004", "\tFiltros s\u00e3o todos opcionais.  Quando v\u00e1rios filtros s\u00e3o usados, eles s\u00e3o   \n\tunidos logicamente com AND." },
      { "BL_VIEW_USAGE_005", "    --minDate=value" },
      { "BL_VIEW_USAGE_006", "\tFiltro baseado na data de cria\u00e7\u00e3o m\u00ednima do registro. O valor deve ser      \n\tespecificado como uma data (por exemplo, --minDate=\"{0}\") ou uma data \n\te hora (por exemplo, --minDate=\"{1}\"). Tamb\u00e9m \u00e9 poss\u00edvel inserir a     \n\tdata e hora no formato ISO-8601; por exemplo, --minDate=\"{2}\" ou \n\t--minDate=\"{3}\"." },
      { "BL_VIEW_USAGE_007", "    --maxDate=value" },
      { "BL_VIEW_USAGE_008", "\tFiltro baseado na data de cria\u00e7\u00e3o m\u00e1xima do registro. O valor deve ser      \n\tespecificado como uma data (por exemplo, --maxDate=\"{0}\") ou uma data \n\te hora (por exemplo, --maxDate=\"{1}\"). Tamb\u00e9m \u00e9 poss\u00edvel inserir a     \n\tdata e hora no formato ISO-8601; por exemplo, --maxDate=\"{2}\" ou \n\t--maxDate=\"{3}\"." },
      { "BL_VIEW_USAGE_009", "    --minLevel=value" },
      { "BL_VIEW_USAGE_010", "\tFiltro baseado no n\u00edvel m\u00ednimo.  O valor deve ser um de                 \n\t{0}." },
      { "BL_VIEW_USAGE_011", "    --maxLevel=value" },
      { "BL_VIEW_USAGE_012", "\tFiltro baseado no n\u00edvel m\u00e1ximo.  O valor deve ser um de                 \n\t{0}." },
      { "BL_VIEW_USAGE_013", "    --includeLogger=value[,value]*" },
      { "BL_VIEW_USAGE_014", "\tInclua registros com o nome de criador de logs especificado.  O valor pode incluir * ou ? como um\n \tcuringa." },
      { "BL_VIEW_USAGE_015", "    --excludeLogger=value[,value]*" },
      { "BL_VIEW_USAGE_016", "\tExclua registros com o nome de criador de logs especificado.  O valor pode incluir * ou ? como um\n \tcuringa." },
      { "BL_VIEW_USAGE_017", "    --includeMessage=value" },
      { "BL_VIEW_USAGE_018", "\tFiltro baseado no nome da mensagem.  O valor pode incluir * ou ? como um curinga." },
      { "BL_VIEW_USAGE_019", "    --includeThread=value" },
      { "BL_VIEW_USAGE_020", "\tInclua registros com o ID de encadeamento especificado.  Os valores devem estar em \n\thexadecimal (por exemplo, --includeThread=2a)." },
      { "BL_VIEW_USAGE_021", "    --includeExtension=name=value[,name=value]*" },
      { "BL_VIEW_USAGE_022", "\tInclua registros com o nome e o valor de extens\u00e3o especificados.  O valor pode incluir * ou ? como um\n \tcuringa." },
      { "BL_VIEW_USAGE_023", "    --includeInstance=value" },
      { "BL_VIEW_USAGE_024", "\tInclua registros da inst\u00e2ncia de servidor especificada.  O valor           \n\tdeve ser \"latest\" ou um ID de inst\u00e2ncia v\u00e1lido.  Execute esse comando \n\tusando a a\u00e7\u00e3o listInstances para ver uma lista de IDs de inst\u00e2ncia v\u00e1lidos." },
      { "BL_VIEW_USAGE_025", "Op\u00e7\u00e3o do monitor:" },
      { "BL_VIEW_USAGE_026", "    --monitor" },
      { "BL_VIEW_USAGE_027", "\tMonitore continuamente o novo conte\u00fado do reposit\u00f3rio e da sa\u00edda conforme \u00e9  \n\tgerado." },
      { "BL_VIEW_USAGE_028", "Op\u00e7\u00f5es de sa\u00edda:" },
      { "BL_VIEW_USAGE_029", "    --format={basic | advanced | CBE-1.0.1}" },
      { "BL_VIEW_USAGE_030", "\tEspecifique o formato de sa\u00edda a ser usado.  \"basic\" \u00e9 o formato padr\u00e3o." },
      { "BL_VIEW_USAGE_031", "    --encoding=value" },
      { "BL_VIEW_USAGE_032", "\tEspecifique a codifica\u00e7\u00e3o de caracteres a ser usada para a sa\u00edda." },
      { "BL_VIEW_USAGE_033", "    --isoDateFormat" },
      { "BL_VIEW_USAGE_034", "\tEspecifique o formato de data e hora ISO-8601 a ser usado para a sa\u00edda." }
   };
}
