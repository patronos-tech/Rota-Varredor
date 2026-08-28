# ROTA VARREDOR

Aplicativo mobile para gestão e otimização de rotas de varriçao manual de ruas.

## Problema Resolvido

Trabalhadores que realizam varriçao manual recebem mapas impressos com um setor contendo múltiplas ruas. Este aplicativo transforma esse processo em uma experiência digital, permitindo:

- Visualização do setor no mapa
- Planejamento inteligente da rota
- Rastreamento de localização via GPS
- Registro de execução em tempo real
- Histórico de execuções
- Aprendizado contínuo para otimização de rotas

## Arquitetura

```
rota-varredor/
├── src/                          # Frontend React Native
│   ├── app/                       # Expo Router screens
│   ├── components/                # Reusable UI components
│   ├── features/                  # Feature modules
│   ├── hooks/                     # Custom React hooks
│   ├── services/                  # API & business logic
│   ├── stores/                    # Zustand state management
│   ├── types/                     # TypeScript types
│   ├── utils/                     # Utility functions
│   ├── constants/                 # App constants
│   └── assets/                    # Images, fonts, etc
│
└── backend/                       # FastAPI Backend
    ├── app/
    │   ├── api/                   # API endpoints
    │   ├── core/                  # Core configuration
    │   ├── models/                # Pydantic models
    │   ├── schemas/               # Request/response schemas
    │   ├── services/              # Business logic
    │   ├── repositories/          # Data access layer
    │   ├── utils/                 # Utilities
    │   └── tests/                 # Test suite
    ├── requirements.txt
    └── main.py
```

## Tecnologias

**Frontend:**
- React Native + Expo
- TypeScript
- Zustand (state management)
- Expo Router (navigation)

**Backend:**
- Python 3.11+
- FastAPI
- MongoDB
- Pydantic

**GPS & Mapas:**
- expo-location (real GPS)
- react-native-maps (mapping)
- Modo demo para simulação

## Instalação

### Frontend

```bash
# Install dependencies
npm install

# Copy environment file
cp .env.example .env
```

### Backend

```bash
cd backend

# Create virtual environment
python -m venv venv
source venv/bin/activate  # On Windows: venv\\Scripts\\activate

# Install dependencies
pip install -r requirements.txt

# Copy environment file
cp .env.example .env
```

## Execução

### Frontend

```bash
# Start Expo
npm start

# Run on Android
npm run android

# Run on iOS
npm run ios

# Run on Web
npm run web
```

### Backend

```bash
cd backend
source venv/bin/activate
python main.py
```

Backend estará disponível em: `http://localhost:8000`
Documentação Swagger: `http://localhost:8000/docs`

## Testes

```bash
# Frontend tests
npm test

# Backend tests
cd backend
pytest
```

## Modo Demo

O aplicativo funciona completamente em modo demo sem dependências externas:

- ✅ Dados de setor pré-carregados
- ✅ Simulação de GPS (movimento manual)
- ✅ Mapas locais (sem API key necessária)
- ✅ Banco de dados local (SQLite fallback)

Para ativar o modo demo, configure:

```bash
APP_MODE=demo
DEMO_ENABLE_GPS_SIMULATION=true
```

## GPS

### Real
- Usa `expo-location` para acesso ao GPS do dispositivo
- Atualização a cada 5 segundos (configurável)
- Exige permissões do usuário

### Simulação (Modo Demo)
- Ferramenta de simulação integrada
- Movimento manual pela tela do mapa
- Perfeito para testes sem hardware de GPS

## Mapas

### Modo Demo
- Utiliza dados local sem API externa
- Renderiza setores, ruas e trechos
- Mostra posição do usuário

### Produção (Futuro)
- Integração com Google Maps ou Mapbox
- Requer configuração de API key

## IA e Aprendizado

### Implementado
- Estatísticas de execução
- Análise de sequência de trechos
- Cálculo de eficiência

### Preparado para Integração
- Serviço de OCR para interpretação de mapas
- Serviço de visão computacional
- Otimização de rotas por ML

## Fluxo Principal

1. **Abrir Setor** → Carrega dados do setor demo ou selecionado
2. **Visualizar Mapa** → Mostra ruas, trechos e rota
3. **Iniciar Execução** → Começa a registrar movimento
4. **Acompanhar GPS** → Mostra posição e rua atual (verde/amarelo/vermelho)
5. **Iniciar Trecho** → Registra número inicial
6. **Finalizar Trecho** → Registra número final e marca concluído
7. **Próximo Trecho** → Avança para o próximo
8. **Concluir Setor** → Gera resumo e salva no histórico
9. **Visualizar Histórico** → Revisa execuções passadas
10. **Atualizar Métricas** → Aprende com dados históricos

## Variáveis de Ambiente

Veja `.env.example` para documentação completa. Principais:

- `BACKEND_URL`: URL do backend
- `MONGODB_URL`: Conexão MongoDB
- `GPS_ENABLED`: Ativar GPS real
- `APP_MODE`: 'demo' ou 'production'
- `DEMO_ENABLE_GPS_SIMULATION`: Ativar simulador GPS

## Limitações Conhecidas

1. **Mapas**: Modo demo usa dados locais. Produção requer API key.
2. **IA**: Estrutura preparada, mas sem ML real implementado inicialmente.
3. **Sincronização**: Funciona offline, sincroniza quando online.
4. **Autenticação**: Modo demo sem autenticação. Estrutura pronta para JWT.

## Próximos Passos Técnicos

1. Implementar autenticação JWT
2. Integrar com API de mapas real (Google Maps/Mapbox)
3. Adicionar OCR para interpretação automática de mapas
4. Implementar Machine Learning para otimização de rotas
5. Criar painel de supervisão web
6. Adicionar notificações push
7. Expandir para múltiplos usuários e equipes

## Contribuindo

Para contribuir ao projeto:

1. Crie uma branch: `git checkout -b feature/sua-feature`
2. Commit: `git commit -m 'feat: descrição da feature'`
3. Push: `git push origin feature/sua-feature`
4. Abra um Pull Request

## Licença

Proprietary - Patronos Tech

## Suporte

Para dúvidas ou problemas, abra uma issue no repositório.
