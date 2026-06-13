import urllib.request
import json
import sys

API_KEY = "cac6bc9551ed4f47be8d13665ccc4f04.s0xfFLuCjorEIkYT"
# Test both endpoints
endpoints = ["https://api.z.ai/api/paas/v4/chat/completions", "https://open.bigmodel.cn/api/paas/v4/chat/completions"]
models = ["glm-4-flash", "glm-4-plus", "glm-4", "glm-4-coder", "glm-3-turbo", "glm-5", "glm-4.5", "glm-4.6", "glm-4.6v"]

for endpoint in endpoints:
    print(f"\n--- Testing Endpoint: {endpoint} ---")
    for model in models:
        req = urllib.request.Request(endpoint, headers={
            "Authorization": f"Bearer {API_KEY}",
            "Content-Type": "application/json"
        }, data=json.dumps({
            "model": model,
            "messages": [{"role": "user", "content": "hi"}]
        }).encode('utf-8'))
        
        try:
            with urllib.request.urlopen(req) as response:
                print(f"[SUCCESS] {model}: {json.loads(response.read())['choices'][0]['message']['content']}")
        except urllib.error.HTTPError as e:
            err = json.loads(e.read().decode())
            print(f"[ERROR] {model}: {err.get('error', {}).get('message', 'Unknown Error')}")
        except Exception as e:
            print(f"[ERROR] {model}: {e}")
