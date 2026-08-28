import logging
from app.core.config import settings

logger = logging.getLogger(__name__)

logging.basicConfig(
    level=settings.LOG_LEVEL.upper(),
    format='%(asctime)s - %(name)s - %(levelname)s - %(message)s'
)
