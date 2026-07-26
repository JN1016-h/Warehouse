import axios from 'axios'
import router from '@/router/router-static'
import storage from '@/utils/storage'
import {Message} from 'element-ui'

const http = axios.create({
	timeout: 1000 * 86400,
	withCredentials: true,
	baseURL: '/springboot38hdw40x',
	headers: {
		'Content-Type': 'application/json; charset=utf-8'
	}
})

let authRedirecting = false

function isAuthPage() {
	const name = router.currentRoute && router.currentRoute.name
	const path = (router.currentRoute && router.currentRoute.path) || ''
	return name === 'login' || name === 'register' || path === '/login' || path === '/register'
}

function redirectToLogin(showToast) {
	if (authRedirecting || isAuthPage()) {
		return
	}
	authRedirecting = true
	// 从未登录进站时静默跳转，避免连弹「请先登录」
	if (showToast) {
		Message.error('请先登录')
	}
	storage.remove('Token')
	router.replace({ name: 'login' }).catch(() => {}).finally(() => {
		setTimeout(() => {
			authRedirecting = false
		}, 1500)
	})
}

/** 登录成功后调用，避免积压 401 把用户再踢回登录页 */
export function clearAuthRedirectLock() {
	authRedirecting = false
}

http.interceptors.request.use(config => {
	const token = storage.get('Token')
	if (token) {
		config.headers['Token'] = token
	} else if (config.headers) {
		delete config.headers['Token']
	}
	return config
}, error => {
	return Promise.reject(error)
})

http.interceptors.response.use(response => {
	if (response.data && response.data.code === 401) {
		const headers = (response.config && response.config.headers) || {}
		const requestToken = headers.Token || headers.token || ''
		const currentToken = storage.get('Token')
		// 登录前发出的请求、或换 Token 后的旧请求，忽略其 401，避免踢掉新会话
		if (currentToken && requestToken !== currentToken) {
			return response
		}
		if (isAuthPage()) {
			return response
		}
		// 无 Token：静默跳转；有 Token 但失效：提示一次
		redirectToLogin(!!currentToken)
	}
	return response
}, error => {
	return Promise.reject(error)
})

export default http
