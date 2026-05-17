# Exemplo do Bridgee Android SDK

Um aplicativo Android completo que demonstra como integrar e usar o **Bridgee SDK** para atribuição de usuários e rastreamento UTM. Este projeto mostra as melhores práticas para implementar o Bridgee SDK em suas aplicações Android.

## 🚀 Principais Funcionalidades

- **Integração Completa do Bridgee SDK** - Mostra configuração e inicialização adequada do SDK
- **Integração com Firebase Analytics** - Demonstra o uso do Firebase como provedor de analytics
- **Coleta de Dados do Usuário** - Interface de formulário para coletar informações do usuário (nome, email, telefone)
- **Recuperação de Dados UTM** - Exibe parâmetros UTM (source, medium, campaign) de matches bem-sucedidos
- **Tratamento de Erros** - Manipulação adequada de callbacks para cenários de sucesso e erro
- **Gradle** - Usa Gradle para gerenciamento de dependências

## 📋 Requisitos

- **Android Studio Arctic Fox+**
- **Android API Level 21+** (Android 5.0+)
- **Java 8+** ou **Kotlin**
- **Projeto Firebase** (para provedor de analytics)

## 🛠️ Instruções de Configuração

### 1. Clonar o Repositório
```bash
git clone https://github.com/bridgee-ai/bridgee-android-example.git
cd bridgee-android-example
```

### 2. Configuração do Firebase
1. Crie um projeto Firebase no [Console do Firebase](https://console.firebase.google.com/)
2. Adicione um app Android com package name: `ai.bridgee.androidexampleapp`
3. Baixe o arquivo `google-services.json`
4. Substitua o arquivo placeholder em `app/google-services.json`

### 3. Configuração do Bridgee SDK
Atualize a configuração em `MainActivity.java`:

```java
private void initializeBridgeeSDK() {
    String tenantId = "your-tenant-id";        // Fornecido pela Bridgee
    String tenantKey = "your-tenant-key";      // Fornecido pela Bridgee
    Boolean dryRun = false;                    // true para testes, false para produção
    
    bridgeeSDK = BridgeeSDK.getInstance(this, analyticsProvider, tenantId, tenantKey, dryRun);
}
```

### 4. Executar o Projeto
1. Abra o projeto no Android Studio
2. Sincronize o projeto com os arquivos Gradle
3. Selecione um emulador ou dispositivo
4. Clique em "Run" ou pressione `Shift+F10`

## 📱 Como Funciona

### Fluxo do Usuário
1. **Inserir Dados do Usuário** - Preencha nome, email e/ou número de telefone
2. **Tocar em "Send First Open"** - Aciona o método `firstOpen` do SDK
3. **Ver Resultados** - Veja os dados UTM (sucesso) ou mensagem de erro

### Resposta de Sucesso
Quando um match é encontrado, o app exibe:
```
🎯 SDK Success

✅ Bridgee SDK Response:

📊 UTM Parameters:
• UTM Source: google
• UTM Medium: cpc
• UTM Campaign: summer_sale
```

### Resposta de Erro
Quando nenhum match é encontrado:
```
⚠️ SDK Error

❌ Bridgee SDK Error:

🚨 Error Message:
First Open without Match - No UTM Data
```

## 🏗️ Guia de Implementação

### 1. Adicionar Dependência do Bridgee SDK
Adicione ao seu `build.gradle` (módulo app):
```gradle
dependencies {
    implementation 'ai.bridgee:bridgee-android-sdk:2.3.0'
}
```

### 2. Configurar o SDK
No seu `MainActivity` ou `Application`:
```java
import ai.bridgee.android.sdk.BridgeeSDK;
import ai.bridgee.android.sdk.AnalyticsProvider;

public class MainActivity extends AppCompatActivity {
    private BridgeeSDK bridgeeSDK;
    private AnalyticsProvider analyticsProvider;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Initialize Firebase Analytics
        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        
        // Initialize Analytics Provider and Bridgee SDK
        initializeAnalyticsProvider();
        initializeBridgeeSDK();
    }
}
```

### 3. Criar Provedor de Analytics
Implemente a interface `AnalyticsProvider`:
```java
private void initializeAnalyticsProvider() {
    analyticsProvider = new AnalyticsProvider() {
        @Override
        public void logEvent(String name, Bundle params) {
            // Delegate to Firebase Analytics
            mFirebaseAnalytics.logEvent(name, params);
        }

        @Override
        public void setUserProperty(String name, String value) {
            // Delegate to Firebase Analytics
            mFirebaseAnalytics.setUserProperty(name, value);
        }
    };
}
```

### 4. Usar o SDK
Chame o método `firstOpen` com dados do usuário:
```java
import ai.bridgee.android.sdk.MatchBundle;
import ai.bridgee.android.sdk.ResponseCallback;
import ai.bridgee.android.sdk.MatchResponse;

private void callBridgeeFirstOpen() {
    // Create MatchBundle with form data
    MatchBundle matchBundle = new MatchBundle()
            .withName("João Silva")
            .withEmail("joao@exemplo.com")
            .withPhone("+5511999999999");

    // Call firstOpen with callback
    bridgeeSDK.firstOpen(matchBundle, new ResponseCallback<MatchResponse>() {
        @Override
        public void ok(MatchResponse response) {
            runOnUiThread(() -> {
                // Sucesso - manipular dados UTM
                Log.i("Bridgee", "UTM Source: " + response.getUtmSource());
                Log.i("Bridgee", "UTM Medium: " + response.getUtmMedium());
                Log.i("Bridgee", "UTM Campaign: " + response.getUtmCampaign());
                showResponseDialog(response);
            });
        }

        @Override
        public void error(Exception e) {
            runOnUiThread(() -> {
                // Erro - nenhum match encontrado
                Log.e("Bridgee", "Erro: " + e.getMessage());
                showErrorDialog(e.getMessage());
            });
        }
    });
}
```

## 📁 Estrutura do Projeto

```
app/
├── src/main/java/ai/bridgee/androidexampleapp/
│   └── MainActivity.java              # UI principal com formulário e integração do SDK
├── src/main/res/
│   ├── layout/
│   │   └── activity_main.xml          # Layout do formulário
│   └── values/
│       └── strings.xml                # Strings da aplicação
├── google-services.json               # Configuração do Firebase (placeholder)
└── build.gradle                       # Dependências e configuração do módulo
```

## 🔧 Componentes Principais

### Configuração do BridgeeSDK
- **Context**: Contexto da aplicação Android
- **AnalyticsProvider**: Provedor de analytics (Firebase neste exemplo)
- **TenantId**: Seu identificador único de tenant da Bridgee
- **TenantKey**: Sua chave de autenticação da Bridgee
- **DryRun**: Defina como `true` para testes, `false` para produção

### MatchBundle
Usado para passar dados do usuário para o SDK. **Todos os campos são opcionais** — o SDK resolve atribuição mesmo com um bundle vazio, mas mais dados = maior confiança no match:
- `withName(String)` - Nome do usuário
- `withEmail(String)` - Email do usuário
- `withPhone(String)` - Telefone do usuário
- `withGclid(String)` - Google Click ID
- `withCustomParam(String, String)` - Parâmetros customizados

> 🔗 **Importante:** os mesmos parâmetros enviados aqui devem ser propagados nas URLs de captura (os **blinks**, ex.: `https://android.seuapp.com.br/?email=...&phone=...&utm_source=...`). O servidor Bridgee compara os sinais dos dois lados (clique vs. instalação) — quanto maior a interseção, mais eficiente e preciso o match.

### Resposta MatchResponse
Contém informações de atribuição:
- `getUtmSource()` - Fonte de tráfego (ex: "google", "facebook")
- `getUtmMedium()` - Meio de marketing (ex: "cpc", "email")
- `getUtmCampaign()` - Nome da campanha (ex: "promocao_verao")

## 🔒 Notas de Segurança

- Nunca faça commit de arquivos reais `google-services.json` no controle de versão
- Mantenha suas credenciais de tenant da Bridgee seguras
- Use variáveis de ambiente ou configuração segura para apps de produção

## 🆘 Troubleshooting

- **`Default FirebaseApp is not initialized`** → o arquivo real `google-services.json` não foi adicionado em `app/`. Substitua o placeholder e rode **File > Sync Project with Gradle Files**.
- **Gradle sync falhou** → em Android Studio: **File > Invalidate Caches / Restart**. Se persistir, apague `~/.gradle/caches` e sincronize de novo.
- **Emulador Android não é detectado** → rode `adb devices`. Se vazio, abra o AVD no Android Studio primeiro ou reinicie o ADB com `adb kill-server && adb start-server`.
- **`Minimum supported Gradle version is X`** → atualize o wrapper: `./gradlew wrapper --gradle-version <versão>`.
- **Erro de compile com `compileSdkVersion`** → confirme que tem o SDK correspondente instalado via **Tools > SDK Manager** no Android Studio.
- **App não recebe UTMs mesmo em produção** → valide no Firebase Analytics DebugView se os dados de aquisição estão chegando (o SDK cuida de propagar os UTMs para o provedor de analytics automaticamente).

## 📚 Recursos Adicionais

- [Documentação do Bridgee Android SDK](https://github.com/bridgee-ai/bridgee-android-sdk)
- [Guia de Setup do Firebase Android](https://firebase.google.com/docs/android/setup)
- [Guia do Gradle](https://gradle.org/guides/)

## 🤝 Contribuindo

1. Faça um fork do repositório
2. Crie uma branch para sua feature
3. Faça suas alterações
4. Envie um pull request
