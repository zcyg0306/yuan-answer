<template>
  <a-button type="outline" @click="handleClick">AI 生成题目</a-button>
  <a-drawer
    :width="340"
    :visible="visible"
    @ok="handleOk"
    @cancel="handleCancel"
    unmountOnClose
  >
    <template #title>AI 生成题目</template>
    <div>
      <a-form
        :model="form"
        label-align="left"
        auto-label-width
        @submit="handleSubmit"
      >
        <a-form-item label="应用 id">
          {{ appId }}
        </a-form-item>
        <a-form-item field="questionNumber" label="题目数量">
          <a-input-number
            min="0"
            max="20"
            v-model="form.questionNumber"
            placeholder="请输入题目数量"
          />
        </a-form-item>
        <a-form-item field="optionNumber" label="选项数量">
          <a-input-number
            min="0"
            max="6"
            v-model="form.optionNumber"
            placeholder="请输入选项数量"
          />
        </a-form-item>
        <a-form-item>
          <a-space>
            <a-button
              :loading="submitting"
              type="primary"
              html-type="submit"
              style="width: 120px"
            >
              {{ submitting ? "生成中" : "一键生成" }}
            </a-button>
            <a-button
              :loading="sseSubmitting"
              style="width: 120px"
              @click="handleSSESubmit"
            >
              {{ sseSubmitting ? "生成中" : "实时生成" }}
            </a-button>
          </a-space>
        </a-form-item>
      </a-form>
    </div>
  </a-drawer>
</template>

<script setup lang="ts">
import { defineProps, reactive, ref, withDefaults } from "vue";
import API from "@/api";
import { aiGenerateQuestionUsingPost } from "@/api/questionController";
import message from "@arco-design/web-vue/es/message";

interface Props {
  appId: string;
  onSuccess?: (result: API.QuestionContentDTO[]) => void;
  onSSESuccess?: (result: API.QuestionContentDTO) => void;
  onSSEStart?: (event: any) => void;
  onSSEClose?: (event: any) => void;
}

const props = withDefaults(defineProps<Props>(), {
  appId: () => {
    return "";
  },
});

const form = reactive({
  optionNumber: 2,
  questionNumber: 10,
} as API.AiGenerateQuestionRequest);

const visible = ref(false);
const submitting = ref(false);
const sseSubmitting = ref(false);

const handleClick = () => {
  visible.value = true;
};
const handleOk = () => {
  visible.value = false;
};
const handleCancel = () => {
  visible.value = false;
};

/**
 * 提交
 */
const handleSubmit = async () => {
  if (!props.appId) {
    return;
  }
  submitting.value = true;
  const res = await aiGenerateQuestionUsingPost({
    appId: props.appId as any,
    ...form,
  });
  if (res.data.code === 0 && res.data.data.length > 0) {
    if (props.onSuccess) {
      props.onSuccess(res.data.data);
    } else {
      message.success("生成题目成功");
    }
    // 关闭抽屉
    handleCancel();
  } else {
    message.error("操作失败，" + res.data.message);
  }
  submitting.value = false;
};

/**
 * 提交（实时生成）
 */
const handleSSESubmit = async () => {
  if (!props.appId) {
    return;
  }
  sseSubmitting.value = true;

  try {
    const response = await fetch(
        // todo 手动填写完整的后端地址
        `http://localhost:8101/api/question/ai_generate/sse?appId=${props.appId}&optionNumber=${form.optionNumber}&questionNumber=${form.questionNumber}`,
        {
          credentials: 'include', // 允许携带 Cookie
        }
    );

    const reader = response.body.getReader();
    let first = true;

    const decoder = new TextDecoder();
    let buffer = ''; // 用于存储 SSE 流中的数据

    const processResult = (result) => {
      if (result.done) {
        props.onSSEClose?.();
        return;
      }

      buffer += decoder.decode(result.value, { stream: true });

      // 将多条 SSE 消息分割
      const lines = buffer.split('\n');
      buffer = lines.pop(); // 保留未处理完的部分

      lines.forEach((line) => {
        // 仅处理以 "data:" 开头的行
        if (line.startsWith('data:')) {
          const data = line.replace('data:', '').trim();
          try {
            const parsedData = JSON.parse(data);
            if (first) {
              props.onSSEStart?.(parsedData);
              handleCancel();
              first = false;
            }
            props.onSSESuccess?.(parsedData);
          } catch (error) {
            console.error('JSON 解析错误', error);
          }
        }
      });

      reader.read().then(processResult);
    };

    reader.read().then(processResult);
  } catch (error) {
    console.error("连接错误", error);
    props.onSSEClose?.();
  }

  sseSubmitting.value = false;
};
</script>
