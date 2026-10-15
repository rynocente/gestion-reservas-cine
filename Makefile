.PHONY: help infra-up infra-down infra-restart infra-clean build docker-rebuild logs ps

# Default target
help:
	@echo ""
	@echo "  Spring Boot Cloud - Comandos disponibles"
	@echo "  -----------------------------------------"
	@echo "  make infra-up                    Levanta toda la infraestructura Docker"
	@echo "  make infra-down                  Detiene y elimina los contenedores"
	@echo "  make infra-restart               Reinicia la infraestructura"
	@echo "  make ps                          Estado de los contenedores"
	@echo "  make logs service=xxx            Tail de logs de un servicio Docker"
	@echo "  make build service=xxx           Compila un servicio Maven (genera el JAR)"
	@echo "  make docker-rebuild service=xxx  Reconstruye la imagen Docker y reinicia el contenedor"
	@echo ""
	@echo "  Ejemplos:"
	@echo "  make logs service=postgres-ms"
	@echo "  make build service=product-service"
	@echo "  make docker-rebuild service=payment-gateway-service"
	@echo ""

infra-up:
	docker compose -f docker/docker-compose.yml up -d
	@echo "Infraestructura levantada. Accesos:"
	@echo "  pgAdmin:  http://localhost:5050"
	@echo "  kafka-ui: http://localhost:9000  (disponible desde modulo 09)"
	@echo "  Grafana:  http://localhost:3000  (disponible desde modulo 10)"
	@echo "  Jaeger:   http://localhost:16686 (disponible desde modulo 10)"

infra-down:
	docker compose -f docker/docker-compose.yml down

# Elimina contenedores Y volúmenes — útil cuando cambia la estructura de la BD
# o se quiere empezar desde cero (re-ejecuta los scripts de init-db)
infra-clean:
	docker compose -f docker/docker-compose.yml down -v
	@echo "Contenedores y volumenes eliminados. Usa make infra-up para empezar desde cero."

infra-restart: infra-down infra-up

ps:
	docker compose -f docker/docker-compose.yml ps

logs:
	docker logs -f $(service)

build:
	cd $(service) && mvn package -DskipTests -q
	@echo "Build de $(service) completado -> $(service)/target/"

# Reconstruye la imagen Docker de un servicio y reinicia solo ese contenedor.
# Usar cuando cambia codigo de un servicio que corre en Docker (ej: payment-gateway-service).
docker-rebuild:
	docker compose -f docker/docker-compose.yml build $(service)
	docker compose -f docker/docker-compose.yml up -d --no-deps $(service)
	@echo "$(service) reconstruido y reiniciado"
