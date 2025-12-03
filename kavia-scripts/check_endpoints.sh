#!/usr/bin/env bash
# PUBLIC_INTERFACE
# check_endpoints.sh - Lightweight HTTP checks for core BankApp endpoints.
#
# Usage:
#   # From the project root, with the app already running:
#   bash kavia-scripts/check_endpoints.sh
#
#   # Override the base URL (e.g., different host/port):
#   BASE_URL=http://localhost:8080/bank-api bash kavia-scripts/check_endpoints.sh
#
# Behavior:
#   - Uses curl to perform GET requests against key endpoints.
#   - Verifies HTTP status codes and simple content assertions.
#   - Produces PASS/FAIL/SKIP lines per check and a final summary.
#   - Exits with code 0 if all checks pass (ignoring skips), 1 otherwise.
#
# Notes:
#   - No authentication headers are sent; relies on SecurityConfig whitelisting.
#   - Default base URL is http://localhost:3001/bank-api.

set -euo pipefail

BASE_URL="${BASE_URL:-http://localhost:3001/bank-api}"
# Trim trailing slash to avoid '//' when joining paths.
BASE_URL="${BASE_URL%/}"

PASS=0
FAIL=0
SKIP=0

log_pass() {
  echo "[PASS] $1"
  PASS=$((PASS + 1))
}

log_fail() {
  echo "[FAIL] $1"
  FAIL=$((FAIL + 1))
}

log_skip() {
  echo "[SKIP] $1"
  SKIP=$((SKIP + 1))
}

# Perform a curl request and capture status code and body.
# Arguments:
#   $1 - URL
#   $2 - Accept header (may be empty)
#   $3 - follow_redirects: "true" or "false"
# Outputs:
#   Sets globals: CURL_STATUS, CURL_BODY_FILE
do_request() {
  local url="$1"
  local accept="$2"
  local follow_redirects="$3"

  CURL_BODY_FILE="$(mktemp)"
  local curl_args=(
    -sS
    -o "$CURL_BODY_FILE"
    -w "%{http_code}"
    --max-time 10
    --connect-timeout 5
  )

  if [[ "$follow_redirects" == "true" ]]; then
    curl_args+=(-L)
  fi

  if [[ -n "$accept" ]]; then
    curl_args+=(-H "Accept: $accept")
  fi

  # Capture HTTP status; on curl error, set status to 000 for clarity.
  local status
  if ! status="$(curl "${curl_args[@]}" "$url" 2>/dev/null)"; then
    status="000"
  fi

  CURL_STATUS="$status"
}

# Verifies: GET /customers returns 200 and a non-empty JSON array.
check_customers() {
  local url="${BASE_URL}/customers"
  do_request "$url" "application/json" "false"

  if [[ "$CURL_STATUS" != "200" ]]; then
    log_fail "GET $url -> HTTP $CURL_STATUS (expected 200)"
    rm -f "$CURL_BODY_FILE"
    return
  fi

  # Check for JSON array format.
  if ! grep -q "\[" "$CURL_BODY_FILE" || ! grep -q "\]" "$CURL_BODY_FILE"; then
    log_fail "GET $url -> 200 but response did not look like a JSON array"
    rm -f "$CURL_BODY_FILE"
    return
  fi

  # Fail if it's an empty array: []
  if grep -Eq '^[[:space:]]*\[[[:space:]]*\][[:space:]]*$' "$CURL_BODY_FILE"; then
    log_fail "GET $url -> 200 but returned an empty JSON array (expected non-empty)"
  else
    log_pass "GET $url -> 200 and returned a non-empty JSON array"
  fi

  rm -f "$CURL_BODY_FILE"
}

# Verifies: GET /accounts returns 200 and a JSON array (may be empty).
check_accounts() {
  local url="${BASE_URL}/accounts"
  do_request "$url" "application/json" "false"

  if [[ "$CURL_STATUS" != "200" ]]; then
    log_fail "GET $url -> HTTP $CURL_STATUS (expected 200)"
    rm -f "$CURL_BODY_FILE"
    return
  fi

  if grep -q "\[" "$CURL_BODY_FILE" && grep -q "\]" "$CURL_BODY_FILE"; then
    log_pass "GET $url -> 200 and JSON array returned"
  else
    log_fail "GET $url -> 200 but response did not look like a JSON array"
  fi

  rm -f "$CURL_BODY_FILE"
}

check_healthz() {
  local url="${BASE_URL}/healthz"
  do_request "$url" "application/json" "false"

  if [[ "$CURL_STATUS" != "200" ]]; then
    log_fail "GET $url -> HTTP $CURL_STATUS (expected 200)"
  elif grep -q '"status"[[:space:]]*:[[:space:]]*"ok"' "$CURL_BODY_FILE"; then
    log_pass "GET $url -> 200 and JSON contains {\"status\":\"ok\"}"
  else
    log_fail "GET $url -> 200 but JSON body did not contain {\"status\":\"ok\"}"
  fi

  rm -f "$CURL_BODY_FILE"
}

check_actuator_health() {
  local url="${BASE_URL}/actuator/health"
  do_request "$url" "application/json" "false"

  if [[ "$CURL_STATUS" != "200" ]]; then
    log_fail "GET $url -> HTTP $CURL_STATUS (expected 200)"
  elif grep -q '"status"' "$CURL_BODY_FILE"; then
    if grep -qi '"status"[[:space:]]*:[[:space:]]*"up"' "$CURL_BODY_FILE"; then
      log_pass "GET $url -> 200 and JSON status is UP"
    else
      log_pass "GET $url -> 200 and JSON contains a 'status' field"
    fi
  else
    log_fail "GET $url -> 200 but JSON body did not contain a 'status' field"
  fi

  rm -f "$CURL_BODY_FILE"
}

check_openapi_docs() {
  local url="${BASE_URL}/v3/api-docs"
  do_request "$url" "application/json" "false"

  if [[ "$CURL_STATUS" != "200" ]]; then
    log_fail "GET $url -> HTTP $CURL_STATUS (expected 200)"
    rm -f "$CURL_BODY_FILE"
    return
  fi

  if grep -q '"openapi"' "$CURL_BODY_FILE"; then
    log_pass "GET $url -> 200 and JSON includes an 'openapi' field"
  elif grep -q '"swagger"' "$CURL_BODY_FILE"; then
    log_skip "GET $url -> 200 but appears to be Swagger 2.x (Springfox); skipping OpenAPI 3 assertion"
  else
    log_fail "GET $url -> 200 but JSON did not contain 'openapi' or 'swagger' field"
  fi

  rm -f "$CURL_BODY_FILE"
}

check_swagger_ui() {
  local url="${BASE_URL}/swagger-ui.html"
  do_request "$url" "text/html" "true"

  if [[ "$CURL_STATUS" != "200" ]]; then
    log_fail "GET $url (with redirects) -> HTTP $CURL_STATUS (expected final 200)"
    rm -f "$CURL_BODY_FILE"
    return
  fi

  if grep -qi "swagger ui" "$CURL_BODY_FILE"; then
    log_pass "GET $url (with redirects) -> Swagger UI HTML reachable"
  else
    log_fail "GET $url (with redirects) -> 200 but HTML did not contain 'Swagger UI'"
  fi

  rm -f "$CURL_BODY_FILE"
}

check_h2_console() {
  local url="${BASE_URL}/h2-console"
  do_request "$url" "text/html" "true"

  if [[ "$CURL_STATUS" != "200" ]]; then
    # H2 often responds with an initial 302 redirect; we follow redirects, so final code should be 200.
    log_fail "GET $url (with redirects) -> HTTP $CURL_STATUS (expected final 200)"
    rm -f "$CURL_BODY_FILE"
    return
  fi

  if grep -qi "h2 console" "$CURL_BODY_FILE"; then
    log_pass "GET $url (with redirects) -> H2 Console HTML reachable"
  else
    log_fail "GET $url (with redirects) -> 200 but HTML did not contain 'H2 Console'"
  fi

  rm -f "$CURL_BODY_FILE"
}

echo "=== BankApp endpoint checks ==="
echo "BASE_URL=${BASE_URL}"
echo

check_customers
check_accounts
check_healthz
check_actuator_health
check_openapi_docs
check_swagger_ui
check_h2_console

echo
echo "Summary: ${PASS} passed, ${FAIL} failed, ${SKIP} skipped"

if [[ "$FAIL" -ne 0 ]]; then
  echo "Result: FAIL"
  exit 1
fi

echo "Result: PASS"
exit 0
