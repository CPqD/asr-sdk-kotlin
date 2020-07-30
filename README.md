# CPqD ASR Recognizer  [![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

*O Recognizer é uma API para criação de aplicações de voz que utilizam o servidor CPqD ASR para reconhecimento de fala.*

Para maiores informações, consulte [a documentação do projeto](https://speechweb.cpqd.com.br/asr/docs).

## Códigos de Exemplo

Os códigos de exemplo estão sob o diretório [sample-app](https://github.com/CPqD/asr-sdk-kotlin/tree/master/sample)


## Instalação

Baixe o conteúdo do repositório em seu computador usando o comando abaixo:

	# git clone https://github.com/CPqD/asr-sdk-kotlin.git
	
Abra o projeto *recognizer* no Android Studio e gere o arquivo aar contendo a biblioteca	

	# Build > Make Project 
	
Adicione o arquivo aar ao seu projeto como uma depêndencia

	# File > Project Structure > Dependencies
	
	
## Exemplo

Devemos inicialmente definir qual modelo de fala que utilizamos.
	
```kotlin
val languageModelList: LanguageModelList = LanguageModelList.Builder()
        .addFromURI("builtin:slm/general")
        .build()
```

A seguir definimos os parametros de configuração.

```kotlin
val recognitionConfig: RecognitionConfig = RecognitionConfig.Builder()
        .accept(ContentTypeConstants.TYPE_JSON)
        .contentType(ContentTypeConstants.TYPE_URI_LIST)
        .waitEndMilis(2000)
        .noInputTimeoutMilis(20000)
        .continuousMode(true)
        .build()
```

E por fim criaremos a instância de reconhecimento de fala

```kotlin
val speech = SpeechRecognizer.Builder()
                    .serverURL("wss://speech.cpqd.com.br/asr/ws/v2/recognize/8k")
                    .credentials(user, password)
                    .config(recognitionConfig, languageModelList)
                    .build()
```


Para utilizar devemos chamar o método de reconhecimento de fala, passando como parametro um arquivo e ou um stream de audio, em formato PCM ou WAV.

```kotlin
val audio = FileAudioSource(applicationContext.assets.open(fileAudio))

speech.recognizer(audio, TYPE_OCTET_STREAM)
```



