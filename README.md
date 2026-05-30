# 🛠️ DevTiles

[![Android](https://img.shields.io/badge/Platform-Android-3DDC84.svg?style=flat-square&logo=android)](https://www.android.com)
[![Kotlin](https://img.shields.io/badge/Language-Kotlin-7F52FF.svg?style=flat-square&logo=kotlin)](https://kotlinlang.org)
[![Compose](https://img.shields.io/badge/UI-Jetpack%20Compose-4285F4.svg?style=flat-square&logo=jetpackcompose)](https://developer.android.com/jetpack/compose)

**DevTiles** é um utilitário leve e eficiente para desenvolvedores Android. Ele adiciona botões (Tiles) ao painel de **Configurações Rápidas** do sistema, permitindo alternar configurações críticas de desenvolvimento com apenas um toque, sem precisar navegar por menus profundos nas configurações do sistema.

---

## ✨ Funcionalidades

O aplicativo fornece três blocos (Tiles) para o painel de Configurações Rápidas:

*   **Modo Desenv.:** Ativa ou desativa as Opções do Desenvolvedor globalmente.
*   **Depuração USB:** Alterna o estado da depuração via ADB através do cabo.
*   **Depuração WiFi:** Alterna a depuração via rede (disponível para Android 11+).

---

## 🚀 Instalação e Configuração

Devido à natureza sensível das configurações que este app manipula, o Android exige uma permissão especial de nível de sistema (`WRITE_SECURE_SETTINGS`).

### 1. Concedendo Permissão via ADB

Após instalar o APK, você deve conceder a permissão manualmente através do seu computador usando o ADB:

```bash
adb shell pm grant com.mss.devtiles android.permission.WRITE_SECURE_SETTINGS
```

### 2. Adicionando os Botões

1.  Desça o painel de notificações duas vezes para abrir as **Configurações Rápidas**.
2.  Toque no ícone de **Editar** (Lápis).
3.  Role até encontrar os blocos do **DevTiles**.
4.  Arraste-os para a área ativa do seu painel.

---

## 🛠️ Detalhes Técnicos

*   **Linguagem:** Kotlin
*   **Interface:** Jetpack Compose (Modern Declative UI)
*   **Arquitetura:** Baseada em `TileService` para integração profunda com a UI do sistema.
*   **Segurança:** Implementa verificações de permissão em tempo real e tratamento de exceções de segurança.
*   **Compatibilidade:** 
    *   Mínimo: Android 7.0 (API 24)
    *   Depuração WiFi: Requer Android 11 (API 30+)

---

## 📸 Interface

O aplicativo possui uma interface simples que:
1.  Verifica o status da permissão em tempo real.
2.  Exibe o comando exato necessário para configurar o app via computador.
3.  Oferece a facilidade de copiar o comando com um clique.

---

## 📄 Licença

Este projeto está sob a licença MIT. Veja o arquivo [LICENSE](LICENSE) para mais detalhes.

---
*Desenvolvido com ❤️ para agilizar o fluxo de trabalho de desenvolvedores Android.*
