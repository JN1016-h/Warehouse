import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
  vus: 10,
  duration: '30s',
  thresholds: {
    http_req_failed: ['rate<0.01'],
    http_req_duration: ['p(95)<800'],
  },
};

const baseUrl = __ENV.BASE_URL || 'http://127.0.0.1:8080/springboot38hdw40x';

export default function () {
  const res1 = http.get(`${baseUrl}/shangpinfenlei/list?page=1&limit=10`);
  check(res1, { '分类列表 200': (r) => r.status === 200 });

  const res2 = http.get(`${baseUrl}/config/list?page=1&limit=5`);
  check(res2, { '配置列表 200': (r) => r.status === 200 });

  sleep(1);
}

