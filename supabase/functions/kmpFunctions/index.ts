import { createClient } from "https://esm.sh/@supabase/supabase-js@2";

const fixedUserId = "02af726d-aa75-4301-a410-049d214841f9";
const entrySelect = "id, user_id, area_id, text, entry_date, created_at, updated_at, duration_seconds, timer_started_at, timer_ends_at, timer_cancelled_at, completed_at";
const maxDurationSeconds = 1440 * 60;
const maxTextLength = 3000;
const validAreas = new Set(["WORK", "BODY", "MIND", "CHARACTER", "LIFE"]);
const jsonHeaders = {
  "Content-Type": "application/json",
  "Access-Control-Allow-Origin": "*",
  "Access-Control-Allow-Methods": "GET, PUT, POST, PATCH, DELETE, OPTIONS",
  "Access-Control-Allow-Headers": "authorization, apikey, content-type",
};

type GenericResponse<T> = {
  data: T | null;
  message: string;
  status: boolean;
};

type DiaryQuote = {
  date: string;
  quote: string;
  createdAt: string;
  updatedAt: string;
};

type DiaryEntry = {
  id: string;
  areaId: string | null;
  text: string;
  entryDate: string;
  createdBy: string | null;
  createdAt: string;
  updatedAt: string;
  durationSeconds: number | null;
  timerStartedAt: string | null;
  timerEndsAt: string | null;
  timerCancelledAt: string | null;
  completedAt: string | null;
};

const supabase = createClient(
  Deno.env.get("SUPABASE_URL")!,
  Deno.env.get("SUPABASE_SERVICE_ROLE_KEY")!,
);

function response<T>(
  data: T | null,
  message: string,
  status: boolean,
  statusCode = 200,
): Response {
  const body: GenericResponse<T> = { data, message, status };
  return new Response(JSON.stringify(body), {
    status: statusCode,
    headers: jsonHeaders,
  });
}

function pathSegments(request: Request): string[] {
  return new URL(request.url).pathname.split("/").filter(Boolean);
}

function isQuoteRoute(segments: string[]): boolean {
  const diaryIndex = segments.indexOf("diary");
  return (
    diaryIndex >= 0 &&
    segments[diaryIndex + 1] === "days" &&
    segments[diaryIndex + 3] === "quote"
  );
}

function isEntryRoute(segments: string[]): boolean {
  const diaryIndex = segments.indexOf("diary");
  if (diaryIndex < 0) return false;
  const route = segments.slice(diaryIndex);
  return (
    (route[1] === "entries" && (
      route.length === 2 || route.length === 3 ||
      (route.length === 5 && route[3] === "timer" && ["start", "cancel", "clear-duration"].includes(route[4]))
    )) ||
    (route.length === 4 && route[1] === "days" && route[3] === "entries")
  );
}

function entryIdFromRequest(request: Request): string | null {
  const segments = pathSegments(request);
  const diaryIndex = segments.indexOf("diary");
  const entryId = diaryIndex >= 0 ? segments[diaryIndex + 2] : null;
  return entryId ?? null;
}

function dateFromRequest(request: Request): string | null {
  const segments = pathSegments(request);
  const daysIndex = segments.indexOf("days");
  const date = daysIndex >= 0 ? segments[daysIndex + 1] : null;
  return date && isValidDate(date) ? date : null;
}

function entryDateFromRequest(request: Request): string | null {
  const date = new URL(request.url).searchParams.get("date");
  return date && isValidDate(date) ? date : null;
}

function isValidDate(value: unknown): value is string {
  if (typeof value !== "string" || !/^\d{4}-\d{2}-\d{2}$/.test(value)) {
    return false;
  }

  const parsed = new Date(`${value}T00:00:00.000Z`);
  return !Number.isNaN(parsed.getTime()) && parsed.toISOString().slice(0, 10) === value;
}

function isValidDuration(value: unknown): value is number {
  return typeof value === "number" && Number.isSafeInteger(value) &&
    value > 0 && value <= maxDurationSeconds;
}

function isEntryBody(body: unknown): body is Record<string, unknown> {
  return body !== null && typeof body === "object" && !Array.isArray(body);
}

function hasClientTimerTimes(body: Record<string, unknown>): boolean {
  return ["timerStartedAt", "timerEndsAt", "timerCancelledAt", "completedAt"]
    .some((key) => key in body);
}

function mapQuote(row: {
  quote_date: string;
  quote: string;
  created_at: string;
  updated_at: string;
}): DiaryQuote {
  return {
    date: row.quote_date,
    quote: row.quote,
    createdAt: row.created_at,
    updatedAt: row.updated_at,
  };
}

function mapEntry(row: {
  id: string;
  user_id: string;
  area_id: string | null;
  text: string;
  entry_date: string;
  created_at: string;
  updated_at: string;
  duration_seconds: number | null;
  timer_started_at: string | null;
  timer_ends_at: string | null;
  timer_cancelled_at: string | null;
  completed_at: string | null;
}): DiaryEntry {
  return {
    id: row.id,
    areaId: row.area_id,
    text: row.text,
    entryDate: row.entry_date,
    createdBy: row.user_id,
    createdAt: row.created_at,
    updatedAt: row.updated_at,
    durationSeconds: row.duration_seconds,
    timerStartedAt: row.timer_started_at,
    timerEndsAt: row.timer_ends_at,
    timerCancelledAt: row.timer_cancelled_at,
    completedAt: row.completed_at,
  };
}

async function getQuote(date: string): Promise<Response> {
  const { data, error } = await supabase
    .from("diary_quotes")
    .select("quote_date, quote, created_at, updated_at")
    .eq("user_id", fixedUserId)
    .eq("quote_date", date)
    .maybeSingle();

  if (error) {
    return response(null, "Günün sözü getirilemedi.", false, 500);
  }

  if (!data) {
    return response(null, "Günün sözü bulunamadı.", false, 404);
  }

  return response(mapQuote(data), "Günün sözü başarıyla getirildi.", true);
}

async function updateQuote(request: Request, date: string): Promise<Response> {
  const body = await request.json().catch(() => null) as Record<string, unknown> | null;
  const quote = typeof body?.quote === "string" ? body.quote.trim() : "";

  if (!quote) {
    return response(null, "Quote alanı boş olamaz.", false, 400);
  }

  const { data, error } = await supabase
    .from("diary_quotes")
    .upsert(
      {
        user_id: fixedUserId,
        quote_date: date,
        quote,
        updated_at: new Date().toISOString(),
      },
      { onConflict: "user_id,quote_date" },
    )
    .select("quote_date, quote, created_at, updated_at")
    .single();

  if (error) {
    return response(null, "Günün sözü kaydedilemedi.", false, 500);
  }

  return response(mapQuote(data), "Günün sözü başarıyla kaydedildi.", true);
}

async function getEntries(date: string): Promise<Response> {
  const { data, error } = await supabase
    .from("diary_entries")
    .select(entrySelect)
    .eq("user_id", fixedUserId)
    .eq("entry_date", date)
    .order("created_at", { ascending: false });

  if (error) {
    console.error("getEntries failed", error);
    return response(null, "Günün diary kayıtları getirilemedi.", false, 500);
  }

  return response(
    (data ?? []).map(mapEntry),
    "Günün diary kayıtları başarıyla getirildi.",
    true,
  );
}

async function exportDiaryArchive(): Promise<Response> {
  const entries: DiaryEntry[] = [];
  const quotes: DiaryQuote[] = [];
  const pageSize = 500;
  const exportedAt = new Date().toISOString();

  for (let offset = 0; ; offset += pageSize) {
    const { data, error } = await supabase
      .from("diary_entries")
      .select(entrySelect)
      .eq("user_id", fixedUserId)
      .lte("created_at", exportedAt)
      .order("entry_date")
      .order("created_at")
      .order("id")
      .range(offset, offset + pageSize - 1);
    if (error || !data) {
      console.error("exportDiaryArchive entries failed", error?.code);
      return response(null, "Günlük arşiv kayıtları getirilemedi.", false, 500);
    }
    entries.push(...data.map(mapEntry));
    if (data.length < pageSize) break;
  }

  for (let offset = 0; ; offset += pageSize) {
    const { data, error } = await supabase
      .from("diary_quotes")
      .select("quote_date, quote, created_at, updated_at")
      .eq("user_id", fixedUserId)
      .lte("created_at", exportedAt)
      .order("quote_date")
      .order("id")
      .range(offset, offset + pageSize - 1);
    if (error || !data) {
      console.error("exportDiaryArchive quotes failed", error?.code);
      return response(null, "Günlük arşiv sözleri getirilemedi.", false, 500);
    }
    quotes.push(...data.map(mapQuote));
    if (data.length < pageSize) break;
  }

  return response({ entries, quotes }, "Günlük arşivi getirildi.", true);
}

async function createEntry(request: Request): Promise<Response> {
  const body: unknown = await request.json().catch(() => null);
  if (!isEntryBody(body) || hasClientTimerTimes(body)) {
    return response(null, "Geçerli bir entry isteği gerekli.", false, 400);
  }
  if (
    (body.areaId != null && typeof body.areaId !== "string") ||
    (body.text !== undefined && typeof body.text !== "string") ||
    (body.durationSeconds != null && !isValidDuration(body.durationSeconds)) ||
    (body.markAsCompleted !== undefined && typeof body.markAsCompleted !== "boolean")
  ) {
    return response(null, "Entry alanları geçersiz.", false, 400);
  }
  const areaId = typeof body?.areaId === "string" ? body.areaId : null;
  const text = typeof body?.text === "string" ? body.text : "";
  const entryDate = typeof body?.entryDate === "string" ? body.entryDate : "";
  const durationSeconds = typeof body?.durationSeconds === "number" ? body.durationSeconds : null;
  const markAsCompleted = body?.markAsCompleted === true;

  if (areaId !== null && !validAreas.has(areaId)) {
    return response(null, "Geçerli bir areaId gerekli.", false, 400);
  }

  if (text.length > maxTextLength) {
    return response(null, `Text ${maxTextLength} karakteri geçemez.`, false, 400);
  }

  if (durationSeconds !== null && !isValidDuration(durationSeconds)) {
    return response(null, "Geçerli bir durationSeconds gerekli.", false, 400);
  }
  if (!areaId && !text.trim() && durationSeconds === null && !markAsCompleted) {
    return response(null, "Entry için alan, not, süre veya tamamlandı seçimi gerekli.", false, 400);
  }

  if (!isValidDate(entryDate)) {
    return response(null, "Geçerli bir entryDate gerekli.", false, 400);
  }

  const { data, error } = await supabase
    .from("diary_entries")
    .insert({
      user_id: fixedUserId,
      entry_date: entryDate,
      area_id: areaId,
      text,
      duration_seconds: durationSeconds,
      completed_at: markAsCompleted ? new Date().toISOString() : null,
    })
    .select(entrySelect)
    .single();

  if (error || !data) {
    console.error("createEntry failed", error?.code);
    if (error?.code === "23514" || error?.code === "22023") {
      return response(null, "Entry alanları geçersiz.", false, 400);
    }
    return response(null, "Diary kaydı oluşturulamadı.", false, 500);
  }

  return response(
    mapEntry(data),
    "Diary kaydı başarıyla oluşturuldu.",
    true,
    201,
  );
}

async function updateEntry(request: Request, entryId: string): Promise<Response> {
  const body: unknown = await request.json().catch(() => null);
  if (!isEntryBody(body) || hasClientTimerTimes(body)) {
    return response(null, "Geçerli bir entry isteği gerekli.", false, 400);
  }
  if (
    (body.areaId !== undefined && body.areaId !== null && typeof body.areaId !== "string") ||
    (body.text !== undefined && typeof body.text !== "string") ||
    (body.entryDate !== undefined && !isValidDate(body.entryDate)) ||
    (body.durationSeconds !== undefined && body.durationSeconds !== null && !isValidDuration(body.durationSeconds)) ||
    (body.markAsCompleted !== undefined && typeof body.markAsCompleted !== "boolean")
  ) {
    return response(null, "Entry alanları geçersiz.", false, 400);
  }
  const areaId = body.areaId === null ? null :
    typeof body.areaId === "string" ? body.areaId : undefined;
  const text = typeof body?.text === "string" ? body.text : undefined;
  const entryDate = typeof body?.entryDate === "string" ? body.entryDate : undefined;
  const durationSeconds = body.durationSeconds === null ? null :
    typeof body.durationSeconds === "number" ? body.durationSeconds : undefined;
  const markAsCompleted = body?.markAsCompleted === true;

  if (
    areaId === undefined &&
    text === undefined &&
    entryDate === undefined &&
    durationSeconds === undefined &&
    body.markAsCompleted === undefined
  ) {
    return response(null, "Güncellenecek alan gerekli.", false, 400);
  }

  if (areaId !== undefined && areaId !== null && !validAreas.has(areaId)) {
    return response(null, "Geçerli bir areaId gerekli.", false, 400);
  }

  if (text !== undefined && text.length > maxTextLength) {
    return response(null, `Text ${maxTextLength} karakteri geçemez.`, false, 400);
  }

  if (entryDate !== undefined && !isValidDate(entryDate)) {
    return response(null, "Geçerli bir entryDate gerekli.", false, 400);
  }

  if (durationSeconds !== undefined && durationSeconds !== null && !isValidDuration(durationSeconds)) {
    return response(null, "Geçerli bir durationSeconds gerekli.", false, 400);
  }

  const updates: Record<string, unknown> = {};
  if (areaId !== undefined) updates.area_id = areaId;
  if (text !== undefined) updates.text = text;
  if (entryDate !== undefined) updates.entry_date = entryDate;
  if (durationSeconds !== undefined) updates.duration_seconds = durationSeconds;
  if (body.markAsCompleted !== undefined) {
    updates.completed_at = markAsCompleted ? new Date().toISOString() : null;
  }
  updates.updated_at = new Date().toISOString();

  const { data, error } = await supabase
    .from("diary_entries")
    .update(updates)
    .eq("id", entryId)
    .eq("user_id", fixedUserId)
    .select(entrySelect)
    .maybeSingle();

  if (error) {
    console.error("updateEntry failed", error.code);
    if (error.code === "22023") {
      return response(null, "Çalışan timer süresi değiştirilemez.", false, 409);
    }
    if (error.code === "23514") {
      return response(null, "Entry veya timer bilgileri bu güncellemeye izin vermiyor.", false, 400);
    }
    return response(null, "Diary kaydı güncellenemedi.", false, 500);
  }

  if (!data) {
    return response(null, "Diary kaydı bulunamadı.", false, 404);
  }

  return response(
    mapEntry(data),
    "Diary kaydı başarıyla güncellendi.",
    true,
  );
}

async function clearEntryDuration(request: Request, entryId: string): Promise<Response> {
  const body: unknown = await request.json().catch(() => null);
  if (!isEntryBody(body) || body.durationSeconds !== null || Object.keys(body).length !== 1) {
    return response(null, "Süre kaldırmak için yalnız durationSeconds: null gerekli.", false, 400);
  }

  const { data, error } = await supabase
    .from("diary_entries")
    .update({ duration_seconds: null, updated_at: new Date().toISOString() })
    .eq("id", entryId)
    .eq("user_id", fixedUserId)
    .select(entrySelect)
    .maybeSingle();

  if (error) {
    console.error("clearEntryDuration failed", error.code);
    if (error.code === "22023") {
      return response(null, "Çalışan timer süresi kaldırılamaz.", false, 409);
    }
    if (error.code === "23514") {
      return response(null, "Timer bilgileri sürenin kaldırılmasına izin vermiyor.", false, 400);
    }
    return response(null, "Diary kaydının süresi kaldırılamadı.", false, 500);
  }
  if (!data) return response(null, "Diary kaydı bulunamadı.", false, 404);

  return response(mapEntry(data), "Diary kaydının süresi kaldırıldı.", true);
}

async function deleteEntry(entryId: string): Promise<Response> {
  const { data, error } = await supabase
    .from("diary_entries")
    .delete()
    .eq("id", entryId)
    .eq("user_id", fixedUserId)
    .select(entrySelect)
    .maybeSingle();

  if (error) {
    return response(null, "Diary kaydı silinemedi.", false, 500);
  }

  if (!data) {
    return response(null, "Diary kaydı bulunamadı.", false, 404);
  }

  return response(
    mapEntry(data),
    "Diary kaydı başarıyla silindi.",
    true,
  );
}

async function startEntryTimer(entryId: string): Promise<Response> {
  const { data, error } = await supabase
    .rpc("start_diary_entry_timer", {
      p_entry_id: entryId,
      p_user_id: fixedUserId,
    })
    .returns<Parameters<typeof mapEntry>[0][]>()
    .single();

  if (error || !data) {
    console.error("startEntryTimer failed", error?.code);
    if (error?.code === "P0002") return response(null, "Diary kaydı bulunamadı.", false, 404);
    if (error?.code === "P0001") {
      return response(null, "Aynı anda yalnız bir timer çalışabilir.", false, 409);
    }
    if (error?.code === "22023") return response(null, "Timer başlatılamadı.", false, 409);
    return response(null, "Timer başlatılamadı.", false, 500);
  }
  return response(mapEntry(data), "Timer başlatıldı.", true);
}

async function cancelEntryTimer(entryId: string): Promise<Response> {
  const { data, error } = await supabase
    .rpc("cancel_diary_entry_timer", {
      p_entry_id: entryId,
      p_user_id: fixedUserId,
    })
    .returns<Parameters<typeof mapEntry>[0][]>()
    .single();

  if (error || !data) {
    console.error("cancelEntryTimer failed", error?.code);
    if (error?.code === "P0002") return response(null, "Diary kaydı bulunamadı.", false, 404);
    if (error?.code === "22023") return response(null, "Aktif timer bulunamadı.", false, 409);
    return response(null, "Timer iptal edilemedi.", false, 500);
  }
  return response(mapEntry(data), "Timer iptal edildi.", true);
}

Deno.serve(async (request) => {
  if (request.method === "OPTIONS") {
    return new Response("ok", { headers: jsonHeaders });
  }

  const segments = pathSegments(request);
  try {
    const diaryRoute = segments.slice(segments.indexOf("diary"));
    if (diaryRoute.length === 2 && diaryRoute[0] === "diary" && diaryRoute[1] === "export") {
      return request.method === "GET"
        ? await exportDiaryArchive()
        : response(null, "Method not allowed.", false, 405);
    }

    if (isQuoteRoute(segments)) {
      const date = dateFromRequest(request);
      if (!date) {
        return response(null, "Geçerli bir tarih gerekli.", false, 400);
      }

      if (request.method === "GET") {
        return getQuote(date);
      }

      if (request.method === "PUT") {
        return updateQuote(request, date);
      }
    }

    if (isEntryRoute(segments)) {
      const route = segments.slice(segments.indexOf("diary"));
      if (request.method === "GET" &&
        ((route[1] === "days" && route.length === 4) || route.length === 2)) {
        const date = route[1] === "days" ? dateFromRequest(request) : entryDateFromRequest(request);
        return date
          ? await getEntries(date)
          : response(null, "Geçerli bir tarih gerekli.", false, 400);
      }
      if (request.method === "POST" && route.length === 2) {
        return await createEntry(request);
      }
      const isItemMethod = route.length === 3 && ["PATCH", "DELETE"].includes(request.method);
      const isTimerMethod = route.length === 5 && request.method === "POST";
      if (isItemMethod || isTimerMethod) {
        const entryId = entryIdFromRequest(request);
        if (!entryId || !/^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$/i.test(entryId)) {
          return response(null, "Geçerli bir entry id gerekli.", false, 400);
        }
        if (request.method === "PATCH") return await updateEntry(request, entryId);
        if (request.method === "DELETE") return await deleteEntry(entryId);
        if (route[4] === "start") return await startEntryTimer(entryId);
        if (route[4] === "cancel") return await cancelEntryTimer(entryId);
        if (route[4] === "clear-duration") return await clearEntryDuration(request, entryId);
      }
    }

    if (!isQuoteRoute(segments) && !isEntryRoute(segments)) {
      return response(null, "Endpoint bulunamadı.", false, 404);
    }

    return response(null, "Method not allowed.", false, 405);
  } catch (error) {
    console.error("kmpFunctions request failed", error instanceof Error ? error.name : "UnknownError");
    return response(null, "Beklenmeyen bir sunucu hatası oluştu.", false, 500);
  }
});
