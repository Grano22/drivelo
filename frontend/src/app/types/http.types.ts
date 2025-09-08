import {z} from "zod";

export const RFC7807ProblemDetailsResponseSchema = z.object({
    type: z.string(),
    title: z.string(),
    status: z.number(),
    detail: z.string(),
    instance: z.string()
});