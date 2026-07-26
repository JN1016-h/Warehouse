import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
  stages: [
    { duration: '20s', target: 5 },
    { duration: '30s', target: 20 },
    { duration: '30s', target: 40 },
    { duration: '20s', target: 0 },
  ],
  thresholds: {
    http_req_failed: ['rate<0.05'],
    http_req_duration: ['p(95)<1500', 'p(99)<3000'],
  },
};

const baseUrl = __ENV.BASE_URL || 'http://127.0.0.1:8080/springboot38hdw40x';

export default function () {
  const endpoints = [
    `${baseUrl}/config/list?page=1&limit=5`,
    `${baseUrl}/shangpinfenlei/list?page=1&limit=10`,
    `${baseUrl}/shangpinxinxi/list?page=1&limit=10`,
    `${baseUrl}/gongyingshang/list?page=1&limit=10`,
  ];
  for (const url of endpoints) {
    const res = http.get(url);
    check(res, {
      'status is 200': (r) => r.status === 200,
      'latency < 2s': (r) => r.timings.duration < 2000,
    });
  }
  sleep(0.5);
}
