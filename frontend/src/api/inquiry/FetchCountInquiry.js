import { call } from '../auth/ApiService';

export default async function FetchCountInquiry({ itemId, onCountFetch }) {
  try {
    if (itemId) {
      const response = await call(`/inquiry/getCountInquiries/${itemId}`);
      if (!response.ok) {
        throw new Error('문의 개수를 가져오는 데 실패했습니다.');
      }

      if (onCountFetch) {
        onCountFetch(response);
      }
    }
  } catch (error) {
    console.error('this error(Count Inquiry): ', error);
  }
}
