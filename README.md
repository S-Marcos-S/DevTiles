#   DevTiles

[![Android](https://img.shields.io/badge/Platform-Android-3DDC84.svg?style=flat-square&logo=android)](https://www.android.com)
[![Kotlin](https://img.shields.io/badge/Language-Kotlin-7F52FF.svg?style=flat-square&logo=kotlin)](https://kotlinlang.org)
[![Compose](https://img.shields.io/badge/UI-Jetpack%20Compose-4285F4.svg?style=flat-square&logo=jetpackcompose)](https://developer.android.com/jetpack/compose)

**DevTiles** é um utilitário avançado e leve para desenvolvedores Android. Ele adiciona botões (Tiles) ao painel de **Configurações Rápidas**, permitindo alternar configurações críticas com um toque, além de oferecer ferramentas para contornar limitações de fabricantes em depuração sem fio.

---

##   Funcionalidades

###   Configurações Rápidas (QS Tiles)
*   **Modo Desenv.:** Alterna as Opções do Desenvolvedor.
*   **Depuração USB:** Alterna a depuração via cabo.
*   **Depuração WiFi:** Alterna a depuração via rede (Android 11+).

###   Recursos Avançados
*   **Modo Legado (Porta 5555):** Força a depuração WiFi na porta clássica 5555, permitindo conexões em dispositivos que não possuem a tela de pareamento (código de 6 dígitos).
*   **Atalhos Inteligentes:** Botões dedicados para **Shizuku** e **Termux** que geram comandos prontos para uso.
*   **Cópia Automática:** Ao abrir as janelas de comando, o código necessário é copiado automaticamente para a área de transferência.
*   **Tema AMOLED:** Fundo preto puro (`#000000`) para economia de bateria e estética moderna.
*   **Interface Imersiva:** Suporte total a *Edge-to-Edge* com barras de sistema transparentes.

---

##   Instalação e Configuração

O Android exige a permissão `WRITE_SECURE_SETTINGS` para que o app funcione.

### 1. Concedendo Permissão via ADB

Conecte o celular ao computador e execute:

```bash
adb shell pm grant com.mss.devtiles android.permission.WRITE_SECURE_SETTINGS
```

### 2. Uso com Termux e Shizuku (Modo Legado)

Se o seu dispositivo não mostra o código de pareamento WiFi:
1.  Abra o **DevTiles** e clique em **"Ativar Porta 5555"**.
2.  Clique no ícone do **Shizuku** ou **Termux** no app.
3.  Vá ao Termux e cole o comando (já estará na sua área de transferência).
4.  Aceite o pop-up de permissão RSA na tela do celular.

---

##   Detalhes Técnicos

*   **Linguagem:** Kotlin
*   **UI:** Jetpack Compose com Material Design 3.
*   **Arquitetura:** `TileService` reativo com `ContentObserver` para atualizações de status em tempo real.
*   **Compatibilidade:** Android 7.0+ (Recursos de WiFi requerem Android 11+).

---

##   Licença

Este projeto está sob a licença MIT. Veja o arquivo [LICENSE](LICENSE) para mais detalhes.

---
#KeepAndroidOpen
