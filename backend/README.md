# Backend - Rota Varredor

FastAPI backend para o sistema de gestão de rotas de varriação.

## Instalação

```bash
cd backend
python -m venv venv
source venv/bin/activate  # On Windows: venv\Scripts\activate
pip install -r requirements.txt
```

## Configuração

```bash
cp .env.example .env
```

## Execução

```bash
python main.py
```

Backend estará disponível em: `http://localhost:8000`
Documentação Swagger: `http://localhost:8000/docs`

## Estrutura

```
backend/
├── app/
│   ├── api/              # API endpoints
│   ├── core/             # Core configs
│   ├── repositories/     # Data access
│   ├── schemas/          # Pydantic schemas
│   ├── services/         # Business logic
│   └── main.py           # FastAPI app
├── main.py               # Entry point
└── requirements.txt      # Dependencies
```
