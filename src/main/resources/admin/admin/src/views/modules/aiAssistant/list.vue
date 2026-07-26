<template>
	<div class="ai-assistant">
		<div class="ai-layout" v-if="canUseAi">
			<aside class="ai-sidebar">
				<div class="sidebar-header">
					<span>历史会话</span>
					<el-button type="primary" size="mini" icon="el-icon-plus" @click="newSession">新对话</el-button>
				</div>
				<div class="session-list">
					<div
						v-for="item in sessions"
						:key="item.id"
						class="session-item"
						:class="{ active: item.id === sessionId }"
						@click="openSession(item.id)">
						<div class="session-title">{{ item.title || '未命名会话' }}</div>
						<div class="session-time">{{ item.updateTime || item.createTime }}</div>
					</div>
					<div v-if="!sessions.length" class="empty-tip">暂无历史会话</div>
				</div>
			</aside>

			<section class="ai-main">
				<div class="ai-toolbar">
					<div class="toolbar-left">
						<span class="brand">AI 智能分析助手</span>
						<el-tag v-if="lastDegraded" size="mini" type="warning">本地统计降级</el-tag>
						<el-tag v-if="lastCached" size="mini">缓存命中</el-tag>
					</div>
					<div class="toolbar-right">
						<el-radio-group v-model="style" size="mini">
							<el-radio-button label="SIMPLE">简洁报表</el-radio-button>
							<el-radio-button label="DETAILED">详细专业</el-radio-button>
						</el-radio-group>
						<el-select v-model="timeRange" size="mini" style="width:110px;margin-left:8px;">
							<el-option label="近月" value="MONTH"></el-option>
							<el-option label="近季" value="QUARTER"></el-option>
							<el-option label="近一年" value="YEAR"></el-option>
						</el-select>
						<el-button size="mini" icon="el-icon-download" :disabled="!lastMessageId" @click="exportReport">导出报告</el-button>
					</div>
				</div>

				<div class="ai-intro">
					<p>可自然语言查询库存、动销、补货、周转与风险预警。</p>
					<p>数字由本地业务库先聚合，大模型仅负责表述；无 API 时自动降级为统计报表。</p>
				</div>

				<div class="quick-chips">
					<el-tag
						v-for="chip in visibleChips"
						:key="chip"
						class="chip"
						effect="plain"
						@click.native="ask(chip)">{{ chip }}</el-tag>
				</div>

				<div class="chat-panel" ref="chatPanel">
					<div v-for="(msg, idx) in messages" :key="idx" class="bubble-row" :class="msg.role">
						<div class="bubble">
							<div class="bubble-meta">{{ msg.role === 'user' ? '我' : 'AI助手' }}</div>
							<div class="bubble-content" v-html="renderContent(msg.content)"></div>
						</div>
					</div>
					<div v-if="loading" class="bubble-row assistant">
						<div class="bubble"><div class="bubble-content">正在分析业务数据…</div></div>
					</div>
					<div v-if="!messages.length && !loading" class="welcome-empty">
						开始提问，或点击上方快捷问题
					</div>
				</div>

				<div class="composer">
					<el-input
						type="textarea"
						:rows="3"
						v-model="question"
						placeholder="例如：近三个月哪些商品滞销？给出补货建议"
						@keydown.ctrl.enter.native="send"
						:disabled="loading"></el-input>
					<div class="composer-actions">
						<span class="hint">Ctrl + Enter 发送</span>
						<el-button type="primary" :loading="loading" @click="send">发送</el-button>
					</div>
				</div>
			</section>
		</div>

		<div v-else class="denied">
			<el-alert title="当前角色未开放 AI 智能分析助手" type="warning" show-icon :closable="false"
				description="按需求规格，经销商默认不开放 AI 菜单。请使用仓库管理员、内部员工或系统管理员账号。"></el-alert>
		</div>
	</div>
</template>

<script>
export default {
	data() {
		return {
			canUseAi: true,
			canAccessFinance: false,
			sessions: [],
			messages: [],
			sessionId: null,
			question: '',
			style: 'SIMPLE',
			timeRange: 'MONTH',
			loading: false,
			lastMessageId: null,
			lastDegraded: false,
			lastCached: false,
			chips: ['滞销商品', '补货建议', '周转概况', '缺货风险', '财务应收应付概况']
		}
	},
	computed: {
		visibleChips() {
			if (this.canAccessFinance) {
				return this.chips
			}
			return this.chips.filter(c => c.indexOf('财务') < 0)
		}
	},
	created() {
		this.loadScope().then(() => {
			if (this.canUseAi) {
				this.loadSessions()
			}
		})
	},
	methods: {
		loadScope() {
			return this.$http({
				url: 'ai/scope',
				method: 'get'
			}).then(({ data }) => {
				if (data && data.code === 0) {
						this.canUseAi = !!data.canUseAi
						this.canAccessFinance = !!data.canAccessFinance
					}
			})
		},
		loadSessions() {
			this.$http({ url: 'ai/sessions', method: 'get' }).then(({ data }) => {
				if (data && data.code === 0) {
					this.sessions = data.data || []
				}
			})
		},
		newSession() {
			this.sessionId = null
			this.messages = []
			this.lastMessageId = null
			this.lastDegraded = false
			this.lastCached = false
		},
		openSession(id) {
			this.sessionId = id
			this.$http({
				url: 'ai/sessions/' + id + '/messages',
				method: 'get'
			}).then(({ data }) => {
				if (data && data.code === 0) {
					this.messages = (data.data || []).map(m => ({
						role: m.role,
						content: m.content
					}))
					const assistants = (data.data || []).filter(m => m.role === 'assistant')
					if (assistants.length) {
						const last = assistants[assistants.length - 1]
						this.lastMessageId = last.id
						this.lastDegraded = last.degraded === 1
					}
					this.$nextTick(this.scrollBottom)
				}
			})
		},
		ask(text) {
			this.question = text
			this.send()
		},
		send() {
			const q = (this.question || '').trim()
			if (!q || this.loading) return
			this.messages.push({ role: 'user', content: q })
			this.question = ''
			this.loading = true
			this.$nextTick(this.scrollBottom)
			this.$http({
				url: 'ai/chat',
				method: 'post',
				data: {
					sessionId: this.sessionId,
					question: q,
					style: this.style,
					timeRange: this.timeRange
				}
			}).then(({ data }) => {
				this.loading = false
				if (!(data && data.code === 0)) {
					this.$message.error((data && data.msg) || '问答失败')
					return
				}
				const d = data.data || {}
				this.sessionId = d.sessionId
				this.lastMessageId = d.messageId
				this.lastDegraded = !!d.degraded
				this.lastCached = !!d.cached
				this.messages.push({ role: 'assistant', content: d.answer || '' })
				this.loadSessions()
				this.$nextTick(this.scrollBottom)
			}).catch(() => {
				this.loading = false
				this.$message.error('网络异常，请稍后重试')
			})
		},
		exportReport() {
			if (!this.lastMessageId) return
			this.$http({
				url: 'ai/report/export',
				method: 'post',
				data: { messageId: this.lastMessageId, format: 'md' },
				responseType: 'blob'
			}).then(res => {
				const blob = new Blob([res.data], { type: 'text/markdown;charset=UTF-8' })
				const url = window.URL.createObjectURL(blob)
				const a = document.createElement('a')
				a.href = url
				a.download = 'ai-report.md'
				a.click()
				window.URL.revokeObjectURL(url)
			}).catch(() => this.$message.error('导出失败'))
		},
		scrollBottom() {
			const el = this.$refs.chatPanel
			if (el) el.scrollTop = el.scrollHeight
		},
		escapeHtml(text) {
			return String(text)
				.replace(/&/g, '&amp;')
				.replace(/</g, '&lt;')
				.replace(/>/g, '&gt;')
		},
		renderContent(content) {
			if (!content) return ''
			const lines = String(content).split('\n')
			let html = ''
			let inTable = false
			for (let i = 0; i < lines.length; i++) {
				const line = lines[i]
				const trimmed = line.trim()
				if (trimmed.startsWith('|') && trimmed.endsWith('|')) {
					if (!inTable) {
						html += '<table class="md-table"><tbody>'
						inTable = true
					}
					if (/^\|?\s*-+/.test(trimmed.replace(/\|/g, '|'))) {
						// separator row
						continue
					}
					const cells = trimmed.split('|').filter((c, idx, arr) => idx > 0 && idx < arr.length - 1)
					html += '<tr>' + cells.map(c => '<td>' + this.escapeHtml(c.trim()) + '</td>').join('') + '</tr>'
					continue
				}
				if (inTable) {
					html += '</tbody></table>'
					inTable = false
				}
				if (trimmed.startsWith('## ')) {
					html += '<h4>' + this.escapeHtml(trimmed.slice(3)) + '</h4>'
				} else if (trimmed.startsWith('### ')) {
					html += '<h5>' + this.escapeHtml(trimmed.slice(4)) + '</h5>'
				} else if (trimmed.startsWith('- ')) {
					html += '<div class="md-li">• ' + this.escapeHtml(trimmed.slice(2)) + '</div>'
				} else if (trimmed === '') {
					html += '<br/>'
				} else {
					html += '<div>' + this.escapeHtml(line) + '</div>'
				}
			}
			if (inTable) html += '</tbody></table>'
			return html
		}
	}
}
</script>

<style scoped>
.ai-assistant {
	height: calc(100vh - 120px);
	padding: 12px 16px;
	box-sizing: border-box;
}
.ai-layout {
	display: flex;
	height: 100%;
	background: #f5f7fa;
	border-radius: 8px;
	overflow: hidden;
	border: 1px solid #e4e7ed;
}
.ai-sidebar {
	width: 240px;
	background: #fff;
	border-right: 1px solid #ebeef5;
	display: flex;
	flex-direction: column;
}
.sidebar-header {
	display: flex;
	justify-content: space-between;
	align-items: center;
	padding: 12px;
	font-weight: 600;
	border-bottom: 1px solid #ebeef5;
}
.session-list {
	flex: 1;
	overflow: auto;
	padding: 8px;
}
.session-item {
	padding: 10px;
	border-radius: 6px;
	cursor: pointer;
	margin-bottom: 6px;
}
.session-item:hover,
.session-item.active {
	background: #ecf5ff;
}
.session-title {
	font-size: 13px;
	color: #303133;
	overflow: hidden;
	text-overflow: ellipsis;
	white-space: nowrap;
}
.session-time {
	font-size: 12px;
	color: #909399;
	margin-top: 4px;
}
.empty-tip, .welcome-empty {
	color: #909399;
	font-size: 13px;
	padding: 24px 16px;
	text-align: center;
}
.ai-intro {
	padding: 10px 14px;
	background: #f0f7ff;
	border-bottom: 1px solid #d9ecff;
	color: #406080;
	font-size: 13px;
	line-height: 1.6;
}
.ai-intro p {
	margin: 0;
}
.ai-intro p + p {
	margin-top: 4px;
}
.ai-main {
	flex: 1;
	display: flex;
	flex-direction: column;
	min-width: 0;
}
.ai-toolbar {
	display: flex;
	justify-content: space-between;
	align-items: center;
	padding: 10px 14px;
	background: #fff;
	border-bottom: 1px solid #ebeef5;
	flex-wrap: wrap;
	gap: 8px;
}
.brand {
	font-size: 16px;
	font-weight: 600;
	margin-right: 8px;
}
.toolbar-left, .toolbar-right {
	display: flex;
	align-items: center;
	gap: 6px;
	flex-wrap: wrap;
}
.quick-chips {
	padding: 8px 14px;
	background: #fff;
	border-bottom: 1px solid #f0f2f5;
}
.chip {
	margin: 0 8px 4px 0;
	cursor: pointer;
}
.chat-panel {
	flex: 1;
	overflow: auto;
	padding: 16px;
}
.bubble-row {
	display: flex;
	margin-bottom: 12px;
}
.bubble-row.user {
	justify-content: flex-end;
}
.bubble {
	max-width: 78%;
	background: #fff;
	border-radius: 10px;
	padding: 10px 12px;
	box-shadow: 0 1px 2px rgba(0,0,0,.04);
}
.bubble-row.user .bubble {
	background: #409eff;
	color: #fff;
}
.bubble-meta {
	font-size: 12px;
	opacity: .75;
	margin-bottom: 4px;
}
.bubble-content {
	font-size: 14px;
	line-height: 1.6;
	word-break: break-word;
}
.composer {
	padding: 12px 14px;
	background: #fff;
	border-top: 1px solid #ebeef5;
}
.composer-actions {
	margin-top: 8px;
	display: flex;
	justify-content: space-between;
	align-items: center;
}
.hint {
	font-size: 12px;
	color: #909399;
}
.denied {
	padding: 40px;
}
.md-table {
	width: 100%;
	border-collapse: collapse;
	margin: 8px 0;
	font-size: 12px;
	background: #fff;
	color: #303133;
}
.md-table td {
	border: 1px solid #ebeef5;
	padding: 4px 6px;
}
.bubble-row.user .md-table,
.bubble-row.user .md-table td {
	color: #303133;
}
.md-li {
	margin: 2px 0;
}
</style>
